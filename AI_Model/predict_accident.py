# =============================================================================
# Accident Occurrence Prediction — CSV Batch Predictor
# =============================================================================
# Requirements:
#   pip install xgboost pandas numpy
#
# Usage:
#   python predict_accident.py
#   python predict_accident.py --input my_data.csv --output results.csv
# =============================================================================

import sys
import json
import argparse
import pandas as pd
import numpy as np
import warnings
warnings.filterwarnings("ignore")

from xgboost import XGBClassifier

# =============================================================================
# CONFIGURATION
# =============================================================================

MODEL_FILE      = "accident_model.json"
ENCODING_FILE   = "segment_encoding.json"
DEFAULT_INPUT   = "input_data.csv"
DEFAULT_OUTPUT  = "prediction_results.csv"

# Must match training feature order (segment_id replaced by segment_accident_rate)
FEATURE_COLS = [
    "month_of_year",
    "day_of_month",
    "day_of_week",
    "time_of_day",
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
    "segment_accident_rate",    # derived from segment_id via encoding map
]

# =============================================================================
# LOOKUP TABLES
# =============================================================================

TIME_OF_DAY = {1:"Morning", 2:"Noon", 3:"Evening", 4:"Night"}
VEHICLE_TYPES = {
    1:"Bicycle", 2:"Bus", 3:"Car", 4:"Lorry/Truck",
    5:"Motorcycle", 6:"Other", 7:"Pedestrian",
    8:"Three Wheeler", 9:"Van", 0:"-",
}
WEATHER = {
    1:"Sunny", 2:"Cloudy", 3:"Rainy", 4:"Heavy Rain",
    5:"Fog/Mist", 6:"Windy", 7:"Storm/Thunderstorm",
}
ROAD_CONDITIONS = {
    1:"Dry", 2:"Wet", 3:"Damaged", 4:"Potholes",
    5:"Slippery", 6:"Construction", 7:"Obstructed", 8:"Flooded",
}

RISK_LEVELS = [
    (20,  "LOW",      "[LOW]     "),
    (45,  "MODERATE", "[MODERATE]"),
    (70,  "HIGH",     "[HIGH]    "),
    (101, "CRITICAL", "[CRITICAL]"),
]

def risk_label(pct):
    for threshold, level, tag in RISK_LEVELS:
        if pct < threshold:
            return level, tag
    return "CRITICAL", "[CRITICAL]"

def separator(char="-", width=70):
    print(char * width)

# =============================================================================
# LOAD MODEL & ENCODING MAP
# =============================================================================

def load_model(model_path, encoding_path):
    # Model
    try:
        model = XGBClassifier()
        model.load_model(model_path)
    except Exception as e:
        print(f"\n  ERROR: Could not load model from '{model_path}'.")
        print(f"         Run train_xgboost.py first to generate the model.")
        print(f"         Detail: {e}\n")
        sys.exit(1)

    # Encoding map
    try:
        with open(encoding_path, "r") as f:
            enc = json.load(f)
        segment_map  = {int(k): v for k, v in enc["segment_map"].items()}
        global_mean  = enc["global_mean"]
    except FileNotFoundError:
        print(f"\n  ERROR: Encoding file '{encoding_path}' not found.")
        print(f"         Run train_xgboost.py first to generate it.\n")
        sys.exit(1)

    return model, segment_map, global_mean

# =============================================================================
# LOAD & VALIDATE INPUT CSV
# =============================================================================

def load_input(path):
    try:
        df = pd.read_csv(path)
    except FileNotFoundError:
        print(f"\n  ERROR: Input file '{path}' not found.\n")
        sys.exit(1)

    required = ["segment_id"] + [c for c in FEATURE_COLS if c != "segment_accident_rate"]
    missing  = [c for c in required if c not in df.columns]
    if missing:
        print(f"\n  ERROR: Input CSV is missing required columns:")
        for col in missing:
            print(f"    - {col}")
        print()
        sys.exit(1)

    return df

# =============================================================================
# APPLY ENCODING & PREDICT
# =============================================================================

def prepare_and_predict(model, df, segment_map, global_mean):
    df = df.copy()

    # Encode segment_id -> segment_accident_rate
    df["segment_accident_rate"] = (
        df["segment_id"]
        .map(segment_map)
        .fillna(global_mean)
    )

    # Flag rows with unknown segment IDs
    known_mask   = df["segment_id"].isin(segment_map)
    unknown_segs = df.loc[~known_mask, "segment_id"].unique()
    if len(unknown_segs) > 0:
        print(f"  NOTE: {len(unknown_segs)} unknown segment ID(s) found "
              f"-> using global mean ({global_mean:.4f}):")
        for s in unknown_segs:
            print(f"         segment_id = {s}")
        print()

    X     = df[FEATURE_COLS].fillna(0)
    probs = model.predict_proba(X)[:, 1]
    return probs, known_mask

# =============================================================================
# PRINT ROW RESULT
# =============================================================================

def print_row_result(row_num, row, prob, is_known_segment):
    pct   = prob * 100
    level, tag = risk_label(pct)

    separator()
    seg_note = "" if is_known_segment else "  [unknown segment -> global mean used]"
    print(f"  Row {row_num:>4}  |  Accident Probability = {pct:.0f}%  |  {tag}{seg_note}")
    separator()

    v_count = int(row.get("vehicle_count", 1))
    vehicles_str = []
    for i in range(1, v_count + 1):
        vt    = int(row.get(f"vehicle_type_{i}", 0))
        vs    = row.get(f"vehicle_speed_{i}", 0)
        vname = VEHICLE_TYPES.get(vt, "Unknown")
        vehicles_str.append(f"{vname} @ {vs} km/h")

    tod     = TIME_OF_DAY.get(int(row.get("time_of_day", 0)), "?")
    weather = WEATHER.get(int(row.get("weather_condition", 0)), "?")
    road    = ROAD_CONDITIONS.get(int(row.get("road_condition", 0)), "?")
    seg_rate = row.get("segment_accident_rate", global_mean if "segment_accident_rate" not in row else row["segment_accident_rate"])

    print(f"  Segment ID     : {int(row['segment_id'])}"
          f"  (accident rate: {row.get('segment_accident_rate', 0):.4f})")
    print(f"  Date           : Month {int(row['month_of_year'])}, "
          f"Day {int(row['day_of_month'])}  (DoW: {int(row['day_of_week'])})")
    print(f"  Time           : {tod}")
    print(f"  Vehicles       : {v_count}  ->  {' | '.join(vehicles_str)}")
    print(f"  Weather        : {weather}")
    print(f"  Road           : {road}")
    print(f"  Seg. Accidents : {int(row['segment_accident_count'])}  "
          f"(density: {row['segment_accident_density']})")
    print()

# =============================================================================
# SAVE OUTPUT CSV
# =============================================================================

def save_output(df, probs, known_mask, path):
    out = df.copy()
    out.insert(0, "row_number", range(1, len(df) + 1))
    out["segment_accident_rate"]    = df["segment_accident_rate"].round(4)
    out["segment_known"]            = known_mask.values
    out["accident_probability_pct"] = [f"{p*100:.1f}%" for p in probs]
    out["risk_level"]               = [risk_label(p * 100)[0] for p in probs]
    out.to_csv(path, index=False)

# =============================================================================
# MAIN
# =============================================================================

def main():
    parser = argparse.ArgumentParser(description="Accident probability predictor — CSV batch mode")
    parser.add_argument("--input",    default=DEFAULT_INPUT,  help=f"Input CSV  (default: {DEFAULT_INPUT})")
    parser.add_argument("--output",   default=DEFAULT_OUTPUT, help=f"Output CSV (default: {DEFAULT_OUTPUT})")
    parser.add_argument("--model",    default=MODEL_FILE,     help=f"Model file (default: {MODEL_FILE})")
    parser.add_argument("--encoding", default=ENCODING_FILE,  help=f"Segment encoding file (default: {ENCODING_FILE})")
    args = parser.parse_args()

    separator("=")
    print("  ACCIDENT OCCURRENCE PREDICTOR  -  CSV Batch Mode")
    print("  Powered by XGBoost  |  segment_id: target-encoded")
    separator("=")
    print()

    model, segment_map, global_mean = load_model(args.model, args.encoding)
    print(f"  [OK] Model loaded          : {args.model}")
    print(f"  [OK] Segment encoding map  : {args.encoding}  ({len(segment_map)} segments)")

    df = load_input(args.input)
    print(f"  [OK] Input file loaded     : {args.input}  ({len(df)} rows)")
    print()

    probs, known_mask = prepare_and_predict(model, df, segment_map, global_mean)

    # Add encoded rate back to df for printing
    df["segment_accident_rate"] = df["segment_id"].map(segment_map).fillna(global_mean)

    separator("=")
    print("  PREDICTION RESULTS")
    separator("=")
    print()

    for idx, (_, row) in enumerate(df.iterrows(), start=1):
        print_row_result(idx, row, probs[idx - 1], known_mask.iloc[idx - 1])

    # Summary
    separator("=")
    print("  SUMMARY")
    separator("=")
    pcts   = probs * 100
    counts = {"LOW": 0, "MODERATE": 0, "HIGH": 0, "CRITICAL": 0}
    for p in pcts:
        lv, _ = risk_label(p)
        counts[lv] += 1

    print(f"\n  Total rows processed : {len(df)}")
    print(f"  Known segments       : {known_mask.sum()} / {len(df)}")
    print(f"  Average probability  : {pcts.mean():.1f}%")
    print(f"  Min / Max            : {pcts.min():.1f}% / {pcts.max():.1f}%")
    print()
    print("  Risk distribution:")
    for level in ["LOW", "MODERATE", "HIGH", "CRITICAL"]:
        bar = "#" * counts[level]
        print(f"    {level:<10} : {counts[level]:>3} rows  {bar}")
    print()

    save_output(df, probs, known_mask, args.output)
    print(f"  [OK] Results saved to      : {args.output}")
    separator("=")
    print()

if __name__ == "__main__":
    main()
