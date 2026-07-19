#!/usr/bin/env python3
"""
decode_key.py — XOR Secret Recovery for OWASP UnCrackable Android Level 3

Technique:
    The native library libfoo.so stores a 24-byte encoded key inside FUN_001012c0.
    The key is XOR'd byte-by-byte with a repeating "pizza" pattern.
    This script reverses that operation to recover the plaintext secret.

Encoded bytes (hex) extracted from Ghidra analysis of FUN_001012c0:
    1d 08 11 13 0f 17 49 15  0d 00 03 19 5a 1d 13 15
    08 0e 5a 00 17 08 13 14

XOR key source:
    Derived from native data section constants (DAT_00107040 / 41 / 42).
    The repeating pattern resolves to b"pizza" (repeated to cover 24 bytes).
"""

# === Encoded secret bytes (24 bytes / 48 hex chars) ===
encoded = bytes.fromhex("1d0811130f1749150d0003195a1d1315080e5a0017081314")

# === XOR key (repeating "pizza", zip() auto-truncates to encoded length) ===
xor_key = b"pizzapizzapizzapizzapizzapizza"

# === Decode: XOR each byte pair ===
secret = bytes(a ^ b for a, b in zip(encoded, xor_key))

print("=" * 50)
print(f"  Encoded bytes : {encoded.hex()}")
print(f"  XOR key used  : {xor_key[:len(encoded)].decode()}")
print(f"  Decoded secret: {secret.decode()}")
print("=" * 50)
print(f"\n🔑 Secret string: {secret.decode()}\n")
print("  → Enter this in the 'Enter the Secret String' field")
print("  → Expected result: 'Success! This is the correct secret.'")
