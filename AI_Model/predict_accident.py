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

MODEL_FILE     = "accident_model.json"
SEGMENTS_FILE  = "known_segments.json"
DEFAULT_INPUT  = "input_data.csv"
DEFAULT_OUTPUT = "prediction_results.csv"

INPUT_COLS = [
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

# =============================================================================
# LOOKUP TABLES
# =============================================================================

TIME_OF_DAY = {1: "Morning", 2: "Noon", 3: "Evening", 4: "Night"}

VEHICLE_TYPES = {
    0: "-", 1: "Bicycle", 2: "Bus", 3: "Car", 4: "Lorry/Truck",
    5: "Motorcycle", 6: "Other", 7: "Pedestrian",
    8: "Three Wheeler", 9: "Van",
}

WEATHER = {
    1: "Sunny", 2: "Cloudy", 3: "Rainy", 4: "Heavy Rain",
    5: "Fog/Mist", 6: "Windy", 7: "Storm/Thunderstorm",
}

ROAD_CONDITIONS = {
    1: "Dry", 2: "Wet", 3: "Damaged", 4: "Potholes",
    5: "Slippery", 6: "Construction", 7: "Obstructed", 8: "Flooded",
}

RISK_LEVELS = [
    (30,  "LOW"),
    (65,  "MODERATE"),
    (75,  "HIGH"),
    (101, "CRITICAL"),
]

def risk_label(pct):
    for threshold, level in RISK_LEVELS:
        if pct <= threshold:
            return level
    return "CRITICAL"

def separator(char="-", width=70):
    print(char * width)

# =============================================================================
# LOAD MODEL & KNOWN SEGMENTS
# =============================================================================

def load_model_and_segments(model_path, segments_path):
    try:
        model = XGBClassifier()
        model.load_model(model_path)
    except Exception as e:
        print(f"\n  ERROR: Could not load model from '{model_path}'.")
        print(f"         Run train_xgboost.py first.")
        print(f"         Detail: {e}\n")
        sys.exit(1)

    try:
        with open(segments_path, "r") as f:
            seg_data = json.load(f)
        known_segments  = seg_data["known_segments"]
        unknown_sentinel = seg_data["unknown_sentinel"]
    except FileNotFoundError:
        print(f"\n  ERROR: Segments file '{segments_path}' not found.")
        print(f"         Run train_xgboost.py first.\n")
        sys.exit(1)

    return model, known_segments, unknown_sentinel

# =============================================================================
# LOAD & VALIDATE INPUT CSV
# =============================================================================

def load_input(path):
    try:
        df = pd.read_csv(path)
    except FileNotFoundError:
        print(f"\n  ERROR: Input file '{path}' not found.\n")
        sys.exit(1)

    missing = [c for c in INPUT_COLS if c not in df.columns]
    if missing:
        print(f"\n  ERROR: Input CSV is missing required columns:")
        for col in missing:
            print(f"    - {col}")
        print()
        sys.exit(1)

    return df[INPUT_COLS]

# =============================================================================
# PREPARE & PREDICT
#   Unknown segment IDs are replaced with the sentinel value (-1) which
#   was included during training — the model has a learned branch for it.
# =============================================================================

def prepare_and_predict(model, df, known_segments, unknown_sentinel):
    X = df.copy()

    # Identify unknown segment IDs
    known_set    = set(known_segments)
    unknown_mask = ~X["segment_id"].isin(known_set)
    unknown_segs = X.loc[unknown_mask, "segment_id"].unique()

    if len(unknown_segs) > 0:
        print(f"  NOTE: {len(unknown_segs)} unknown segment ID(s) found "
              f"-> replaced with sentinel ({unknown_sentinel}):")
        for s in unknown_segs:
            print(f"        segment_id = {s}")
        print()
        X.loc[unknown_mask, "segment_id"] = unknown_sentinel

    # Cast to pd.Categorical using the full domain (known + sentinel)
    all_categories = known_segments + [unknown_sentinel]
    X["segment_id"] = pd.Categorical(X["segment_id"], categories=all_categories)

    probs = model.predict_proba(X)[:, 1]
    return probs, unknown_mask.values

# =============================================================================
# PRINT ROW RESULT
# =============================================================================

def print_row_result(row_num, row, prob, is_unknown_seg):
    pct   = prob * 100
    level = risk_label(pct)

    separator()
    seg_note = "  [unknown segment]" if is_unknown_seg else ""
    print(f"  Row {row_num:>4}  |  Accident Probability = {pct:.0f}%  |  [{level}]")
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

    print(f"  Segment ID     : {int(row['segment_id'])}{seg_note}")
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

def save_output(df, probs, unknown_mask, path):
    out = df.copy()
    out.insert(0, "row_number", range(1, len(df) + 1))
    out["segment_known"]            = ~unknown_mask
    out["accident_probability_pct"] = [f"{p * 100:.1f}%" for p in probs]
    out["risk_level"]               = [risk_label(p * 100) for p in probs]
    out.to_csv(path, index=False)

# =============================================================================
# MAIN
# =============================================================================

def main():
    parser = argparse.ArgumentParser(
        description="Accident probability predictor — CSV batch mode"
    )
    parser.add_argument("--input",    default=DEFAULT_INPUT,
                        help=f"Input CSV      (default: {DEFAULT_INPUT})")
    parser.add_argument("--output",   default=DEFAULT_OUTPUT,
                        help=f"Output CSV     (default: {DEFAULT_OUTPUT})")
    parser.add_argument("--model",    default=MODEL_FILE,
                        help=f"Model file     (default: {MODEL_FILE})")
    parser.add_argument("--segments", default=SEGMENTS_FILE,
                        help=f"Segments file  (default: {SEGMENTS_FILE})")
    args = parser.parse_args()

    separator("=")
    print("  ACCIDENT OCCURRENCE PREDICTOR  -  CSV Batch Mode")
    print("  Powered by XGBoost")
    separator("=")
    print()

    model, known_segments, unknown_sentinel = load_model_and_segments(
        args.model, args.segments
    )
    print(f"  [OK] Model loaded      : {args.model}")
    print(f"  [OK] Segments loaded   : {args.segments}  "
          f"({len(known_segments)} known segments, sentinel = {unknown_sentinel})")

    df = load_input(args.input)
    print(f"  [OK] Input file loaded : {args.input}  ({len(df)} rows)")
    print()

    probs, unknown_mask = prepare_and_predict(model, df, known_segments, unknown_sentinel)

    separator("=")
    print("  PREDICTION RESULTS")
    separator("=")
    print()

    for idx, (_, row) in enumerate(df.iterrows(), start=1):
        print_row_result(idx, row, probs[idx - 1], unknown_mask[idx - 1])

    # Summary
    separator("=")
    print("  SUMMARY")
    separator("=")
    pcts   = probs * 100
    counts = {"LOW": 0, "MODERATE": 0, "HIGH": 0, "CRITICAL": 0}
    for p in pcts:
        counts[risk_label(p)] += 1

    known_count   = int((~unknown_mask).sum())
    unknown_count = int(unknown_mask.sum())

    print(f"\n  Total rows processed  : {len(df)}")
    print(f"  Known segments        : {known_count}")
    print(f"  Unknown segments      : {unknown_count}")
    print(f"  Average probability   : {pcts.mean():.1f}%")
    print(f"  Min / Max             : {pcts.min():.1f}% / {pcts.max():.1f}%")
    print()
    print("  Risk distribution:")
    for level in ["LOW", "MODERATE", "HIGH", "CRITICAL"]:
        bar = "#" * counts[level]
        print(f"    [{level:<8}]  {counts[level]:>3} rows  {bar}")
    print()

    save_output(df, probs, unknown_mask, args.output)
    print(f"  [OK] Results saved to  : {args.output}")
    separator("=")
    print()

if __name__ == "__main__":
    main()
