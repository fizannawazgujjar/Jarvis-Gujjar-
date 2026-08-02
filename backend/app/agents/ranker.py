"""
Simple ranking utilities for Commander agent aggregation.

Current implementation is lightweight: rank parts by (has_result, not error) and length of result.
Can be extended with learned scoring or provider confidence signals.
"""
from typing import Dict, Any, List, Tuple


def score_part(name: str, part: Dict[str, Any]) -> float:
    # High-level heuristic scoring
    if part is None:
        return 0.0
    if part.get("error"):
        return 0.0
    result = part.get("result") or part.get("stdout") or ""
    # presence score
    score = 0.0
    if result:
        score += 1.0
        # longer results get a small boost
        try:
            score += min(len(str(result)) / 1000.0, 1.0)
        except Exception:
            pass
    return score


def rank_results(parts: Dict[str, Any]) -> List[Tuple[str, Dict[str, Any], float]]:
    scored = []
    for name, part in parts.items():
        s = score_part(name, part)
        scored.append((name, part, s))
    scored.sort(key=lambda x: x[2], reverse=True)
    return scored
