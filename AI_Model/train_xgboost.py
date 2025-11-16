# =============================================================================
# Accident Occurrence Prediction — XGBoost Classifier
# =============================================================================
# Requirements:
#   pip install xgboost scikit-learn pandas numpy matplotlib seaborn
# =============================================================================

import os
import json
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
import warnings
warnings.filterwarnings("ignore")

from xgboost import XGBClassifier
from sklearn.metrics import (
    accuracy_score, precision_score, recall_score,
    f1_score, roc_auc_score, roc_curve,
    confusion_matrix, classification_report,
)

# =============================================================================
# 1. CONFIGURATION
# =============================================================================

TRAIN_CSV       = "train_data.csv"
TEST_CSV        = "test_data.csv"
MODEL_FILE      = "accident_model.json"
ENCODING_FILE   = "segment_encoding.json"   # saved target-encoding map
TARGET_COL      = "accident_occurrence"

# Columns to drop before training
DROP_COLS = [
    "location_lat",
    "location_lon",
    # segment_id is NOT dropped — it will be target-encoded below
]

XGB_PARAMS = {
    "n_estimators"      : 300,
    "max_depth"         : 6,
    "learning_rate"     : 0.05,
    "subsample"         : 0.8,
    "colsample_bytree"  : 0.8,
    "min_child_weight"  : 3,
    "gamma"             : 0.1,
    "reg_alpha"         : 0.1,
    "reg_lambda"        : 1.0,
    "use_label_encoder" : False,
    "eval_metric"       : "logloss",
    "random_state"      : 42,
    "n_jobs"            : -1,
}

# =============================================================================
# 2. LOAD DATA
# =============================================================================

print("=" * 65)
print("  ACCIDENT OCCURRENCE PREDICTION — XGBoost  (with segment_id)")
print("=" * 65)

print(f"\n[1] Loading data ...")
train_df = pd.read_csv(TRAIN_CSV)
test_df  = pd.read_csv(TEST_CSV)

print(f"    Train shape : {train_df.shape}")
print(f"    Test  shape : {test_df.shape}")
print(f"\n    Train class distribution:")
print(train_df[TARGET_COL].value_counts().rename({0:"No Accident (0)", 1:"Accident (1)"}))

# =============================================================================
# 3. TARGET ENCODING FOR segment_id
#    Mean accident rate per segment (computed ONLY on training data to avoid
#    data leakage). Unknown segment IDs in test set fall back to global mean.
# =============================================================================

print(f"\n[2] Target-encoding segment_id ...")

global_mean = train_df[TARGET_COL].mean()

segment_map = (
    train_df.groupby("segment_id")[TARGET_COL]
    .mean()
    .round(6)
    .to_dict()
)

print(f"    Unique segments in train : {len(segment_map)}")
print(f"    Global accident mean     : {global_mean:.4f}")

# Apply encoding — unseen segments get global mean
train_df["segment_accident_rate"] = (
    train_df["segment_id"].map(segment_map).fillna(global_mean)
)
test_df["segment_accident_rate"] = (
    test_df["segment_id"].map(segment_map).fillna(global_mean)
)

# Save encoding map for use in prediction script
encoding_data = {
    "segment_map"  : {str(k): v for k, v in segment_map.items()},
    "global_mean"  : global_mean,
}
with open(ENCODING_FILE, "w") as f:
    json.dump(encoding_data, f, indent=2)
print(f"    Encoding map saved       : {ENCODING_FILE}")

# =============================================================================
# 4. FEATURE PREPARATION
# =============================================================================

print(f"\n[3] Preparing features ...")

def prepare_features(df, drop_cols, target_col):
    cols_to_drop = [c for c in drop_cols + [target_col, "segment_id"] if c in df.columns]
    X = df.drop(columns=cols_to_drop)
    y = df[target_col]
    return X, y

X_train, y_train = prepare_features(train_df, DROP_COLS, TARGET_COL)
X_test,  y_test  = prepare_features(test_df,  DROP_COLS, TARGET_COL)

print(f"    Feature columns ({len(X_train.columns)}):")
for col in X_train.columns:
    marker = "  [NEW]" if col == "segment_accident_rate" else ""
    print(f"      - {col}{marker}")

# =============================================================================
# 5. HANDLE CLASS IMBALANCE
# =============================================================================

neg = (y_train == 0).sum()
pos = (y_train == 1).sum()
scale_pos_weight = round(neg / pos, 4)
print(f"\n    Class imbalance ratio (neg/pos): {scale_pos_weight}")
XGB_PARAMS["scale_pos_weight"] = scale_pos_weight

# =============================================================================
# 6. TRAIN MODEL
# =============================================================================

print(f"\n[4] Training XGBoost model ...")
model = XGBClassifier(**XGB_PARAMS)
model.fit(
    X_train, y_train,
    eval_set=[(X_train, y_train), (X_test, y_test)],
    verbose=False,
)
print("    Training complete.")

# =============================================================================
# 7. SAVE MODEL
# =============================================================================

model.save_model(MODEL_FILE)
print(f"\n[5] Model saved -> {MODEL_FILE}")

# =============================================================================
# 8. PREDICTIONS & METRICS
# =============================================================================

y_pred       = model.predict(X_test)
y_pred_proba = model.predict_proba(X_test)[:, 1]

print("\n" + "=" * 65)
print("  MODEL EVALUATION ON TEST SET")
print("=" * 65)

accuracy  = accuracy_score(y_test, y_pred)
precision = precision_score(y_test, y_pred, zero_division=0)
recall    = recall_score(y_test, y_pred, zero_division=0)
f1        = f1_score(y_test, y_pred, zero_division=0)
roc_auc   = roc_auc_score(y_test, y_pred_proba)

print(f"\n  Accuracy        : {accuracy  * 100:.2f}%")
print(f"  Precision       : {precision * 100:.2f}%")
print(f"  Recall          : {recall    * 100:.2f}%")
print(f"  F1 Score        : {f1        * 100:.2f}%")
print(f"  ROC-AUC Score   : {roc_auc   * 100:.2f}%")
print(f"\n  Classification Report:\n")
print(classification_report(y_test, y_pred, target_names=["No Accident", "Accident"]))

# =============================================================================
# 9. PLOTS
# =============================================================================

fig, axes = plt.subplots(1, 3, figsize=(18, 5))
fig.suptitle("XGBoost — Accident Occurrence Prediction (with segment_id encoding)",
             fontsize=13, fontweight="bold")

# Confusion matrix
cm = confusion_matrix(y_test, y_pred)
sns.heatmap(cm, annot=True, fmt="d", cmap="Blues",
            xticklabels=["No Accident","Accident"],
            yticklabels=["No Accident","Accident"], ax=axes[0])
axes[0].set_title("Confusion Matrix")
axes[0].set_xlabel("Predicted")
axes[0].set_ylabel("Actual")

# ROC curve
fpr, tpr, _ = roc_curve(y_test, y_pred_proba)
axes[1].plot(fpr, tpr, color="darkorange", lw=2, label=f"AUC = {roc_auc:.3f}")
axes[1].plot([0,1],[0,1], color="navy", lw=1, linestyle="--", label="Random")
axes[1].set_xlim([0.0,1.0]); axes[1].set_ylim([0.0,1.05])
axes[1].set_xlabel("False Positive Rate"); axes[1].set_ylabel("True Positive Rate")
axes[1].set_title("ROC Curve"); axes[1].legend(loc="lower right")

# Feature importances
importance_df = pd.DataFrame({
    "feature"   : X_train.columns,
    "importance": model.feature_importances_,
}).sort_values("importance", ascending=False).head(15)

colors = ["gold" if f == "segment_accident_rate" else "steelblue"
          for f in importance_df["feature"]]
sns.barplot(data=importance_df, x="importance", y="feature",
            palette=colors, ax=axes[2])
axes[2].set_title("Top 15 Feature Importances\n(gold = segment_accident_rate)")
axes[2].set_xlabel("Importance Score"); axes[2].set_ylabel("")

plt.tight_layout()
plt.savefig("xgboost_evaluation.png", dpi=150, bbox_inches="tight")
plt.show()
print(f"\n[6] Plots saved -> xgboost_evaluation.png")

# =============================================================================
# 10. SUMMARY
# =============================================================================

print("\n" + "=" * 65)
print("  SUMMARY")
print("=" * 65)
summary = pd.DataFrame({
    "Metric": ["Accuracy","Precision","Recall","F1 Score","ROC-AUC"],
    "Score" : [f"{accuracy*100:.2f}%", f"{precision*100:.2f}%",
               f"{recall*100:.2f}%",   f"{f1*100:.2f}%",
               f"{roc_auc*100:.2f}%"],
})
print(summary.to_string(index=False))
print("\nDone.\n")
