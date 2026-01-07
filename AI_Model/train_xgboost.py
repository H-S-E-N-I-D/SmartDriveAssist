# =============================================================================
# Accident Occurrence Prediction — XGBoost Classifier
# =============================================================================
# Requirements:
#   pip install xgboost scikit-learn pandas numpy matplotlib seaborn
# =============================================================================

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

TRAIN_CSV      = "train_data.csv"
TEST_CSV       = "test_data.csv"
MODEL_FILE     = "accident_model.json"
SEGMENTS_FILE  = "known_segments.json"   # list of segment IDs seen during training
TARGET_COL     = "accident_occurrence"
UNKNOWN_SEG_ID = -1                      # sentinel for unseen segment IDs

FEATURE_COLS = [
    "month_of_year",
    "day_of_month",
    "day_of_week",
    "time_of_day",
    "segment_id",
    "vehicle_count",
    "vehicle_type_1",
    "vehicle_type_2",
    "vehicle_type_3",
    "vehicle_speed_1",
    "vehicle_speed_2",
    "vehicle_speed_3",
    "weather_condition",
    "road_condition",
    "segment_accident_count",
    "segment_accident_density",
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
    "tree_method"       : "hist",
    "enable_categorical": True,
    "eval_metric"       : "logloss",
    "random_state"      : 42,
    "n_jobs"            : -1,
}

# =============================================================================
# 2. LOAD DATA
# =============================================================================

print("=" * 65)
print("  ACCIDENT OCCURRENCE PREDICTION — XGBoost")
print("=" * 65)

print("\n[1] Loading data ...")
train_df = pd.read_csv(TRAIN_CSV)
test_df  = pd.read_csv(TEST_CSV)

print(f"    Train shape : {train_df.shape}")
print(f"    Test  shape : {test_df.shape}")
print(f"\n    Train class distribution:")
print(train_df[TARGET_COL].value_counts().rename({0: "No Accident (0)", 1: "Accident (1)"}))

# =============================================================================
# 3. PREPARE SEGMENT IDs
#    - Save the set of known segment IDs from training data.
#    - Add sentinel row (UNKNOWN_SEG_ID = -1) so the model learns a branch
#      for unseen segments at prediction time.
#    - Cast segment_id as pandas 'category' for XGBoost native handling.
# =============================================================================

print("\n[2] Preparing segment_id ...")

known_segments = sorted(train_df["segment_id"].unique().tolist())
print(f"    Unique segment IDs in train : {len(known_segments)}")
print(f"    Sentinel for unknown IDs    : {UNKNOWN_SEG_ID}")

# Save known segments so predict script can map unknowns to sentinel
with open(SEGMENTS_FILE, "w") as f:
    json.dump({"known_segments": known_segments, "unknown_sentinel": UNKNOWN_SEG_ID}, f, indent=2)
print(f"    Known segments saved        : {SEGMENTS_FILE}")

# Add a small number of sentinel rows to training data so the model
# has a learned branch for unknown segments
sentinel_rows = train_df.sample(n=min(20, len(train_df)), random_state=42).copy()
sentinel_rows["segment_id"] = UNKNOWN_SEG_ID
train_df = pd.concat([train_df, sentinel_rows], ignore_index=True)
print(f"    Sentinel rows added to train: {len(sentinel_rows)}")

# Cast segment_id as category in both sets using the full category list
# (known segments + sentinel) so both share the same dtype domain
all_seg_cats = known_segments + [UNKNOWN_SEG_ID]

def cast_segment_category(df, categories):
    df = df.copy()
    df["segment_id"] = pd.Categorical(df["segment_id"], categories=categories)
    return df

train_df = cast_segment_category(train_df, all_seg_cats)
test_df  = cast_segment_category(test_df,  all_seg_cats)

# =============================================================================
# 4. PREPARE FEATURES & TARGET
# =============================================================================

print("\n[3] Preparing features ...")

X_train = train_df[FEATURE_COLS]
y_train = train_df[TARGET_COL]
X_test  = test_df[FEATURE_COLS]
y_test  = test_df[TARGET_COL]

print(f"    Feature columns ({len(FEATURE_COLS)}):")
for col in FEATURE_COLS:
    note = "  [categorical — native XGBoost]" if col == "segment_id" else ""
    print(f"      - {col}{note}")

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

print("\n[4] Training XGBoost model ...")
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

print(f"\n  Accuracy      : {accuracy  * 100:.2f}%")
print(f"  Precision     : {precision * 100:.2f}%")
print(f"  Recall        : {recall    * 100:.2f}%")
print(f"  F1 Score      : {f1        * 100:.2f}%")
print(f"  ROC-AUC       : {roc_auc   * 100:.2f}%")
print(f"\n  Classification Report:\n")
print(classification_report(y_test, y_pred, target_names=["No Accident", "Accident"]))

# =============================================================================
# 9. PLOTS
# =============================================================================

fig, axes = plt.subplots(1, 3, figsize=(18, 5))
fig.suptitle("XGBoost — Accident Occurrence Prediction", fontsize=13, fontweight="bold")

# Confusion matrix
cm = confusion_matrix(y_test, y_pred)
sns.heatmap(cm, annot=True, fmt="d", cmap="Blues",
            xticklabels=["No Accident", "Accident"],
            yticklabels=["No Accident", "Accident"], ax=axes[0])
axes[0].set_title("Confusion Matrix")
axes[0].set_xlabel("Predicted")
axes[0].set_ylabel("Actual")

# ROC curve
fpr, tpr, _ = roc_curve(y_test, y_pred_proba)
axes[1].plot(fpr, tpr, color="darkorange", lw=2, label=f"AUC = {roc_auc:.3f}")
axes[1].plot([0, 1], [0, 1], color="navy", lw=1, linestyle="--", label="Random")
axes[1].set_xlim([0.0, 1.0])
axes[1].set_ylim([0.0, 1.05])
axes[1].set_xlabel("False Positive Rate")
axes[1].set_ylabel("True Positive Rate")
axes[1].set_title("ROC Curve")
axes[1].legend(loc="lower right")

# Feature importances
importance_df = pd.DataFrame({
    "feature"   : FEATURE_COLS,
    "importance": model.feature_importances_,
}).sort_values("importance", ascending=False).head(15)

colors = ["gold" if f == "segment_id" else "steelblue"
          for f in importance_df["feature"]]
sns.barplot(data=importance_df, x="importance", y="feature",
            palette=colors, ax=axes[2])
axes[2].set_title("Top 15 Feature Importances\n(gold = segment_id)")
axes[2].set_xlabel("Importance Score")
axes[2].set_ylabel("")

plt.tight_layout()
plt.savefig("xgboost_evaluation.png", dpi=150, bbox_inches="tight")
plt.show()
print("\n[6] Plots saved -> xgboost_evaluation.png")

# =============================================================================
# 10. SUMMARY
# =============================================================================

print("\n" + "=" * 65)
print("  SUMMARY")
print("=" * 65)
summary = pd.DataFrame({
    "Metric": ["Accuracy", "Precision", "Recall", "F1 Score", "ROC-AUC"],
    "Score" : [f"{accuracy*100:.2f}%", f"{precision*100:.2f}%",
               f"{recall*100:.2f}%",   f"{f1*100:.2f}%",
               f"{roc_auc*100:.2f}%"],
})
print(summary.to_string(index=False))
print("\nDone.\n")
