#!/usr/bin/env bash
set -e

echo "=========================================="
echo " Building Stranger Things Project (Unix) "
echo "=========================================="

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

chmod +x ./mvnw

./mvnw clean test

echo ""
echo "BUILD & TESTS SUCCESSFUL!"