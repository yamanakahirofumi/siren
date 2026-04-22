#!/bin/bash

# Get latest version from npm using jq
LATEST_VERSION=$(curl -s https://registry.npmjs.org/mermaid/latest | jq -r '.version')

if [ -z "$LATEST_VERSION" ] || [ "$LATEST_VERSION" == "null" ]; then
    echo "Failed to fetch the latest version of mermaid."
    exit 1
fi

echo "Latest version of mermaid: $LATEST_VERSION"

# Path to mermaid.min.js
MERMAID_JS_PATH="src/main/resources/mermaid.min.js"

# Get current version from one of the files
# Using a more portable regex for sed and grep
CURRENT_VERSION=$(grep -oE 'v[0-9]+\.[0-9]+\.[0-9]+' README.md | head -n 1 | sed 's/v//')

if [ "$CURRENT_VERSION" == "$LATEST_VERSION" ]; then
    echo "Current version is already $LATEST_VERSION. No update needed."
fi

# Download the latest version
echo "Downloading mermaid.min.js version $LATEST_VERSION..."
curl -s -o "$MERMAID_JS_PATH" "https://cdn.jsdelivr.net/npm/mermaid@${LATEST_VERSION}/dist/mermaid.min.js"

if [ $? -eq 0 ]; then
    echo "Successfully downloaded mermaid.min.js version $LATEST_VERSION"
else
    echo "Failed to download mermaid.min.js version $LATEST_VERSION"
    exit 1
fi

# Update version strings in files
FILES_TO_UPDATE=(
    "README.md"
    "docs/spec.md"
)

# Detect OS for sed -i
if [[ "$OSTYPE" == "darwin"* ]]; then
    SED_OPTS=(-i "")
else
    SED_OPTS=(-i)
fi

echo "Updating version strings in files..."
for file in "${FILES_TO_UPDATE[@]}"; do
    if [ -f "$file" ]; then
        echo "Updating $file..."
        # Using a more portable regex for sed: [0-9][0-9]* instead of [0-9]\+
        sed "${SED_OPTS[@]}" "s/v[0-9][0-9]*\.[0-9][0-9]*\.[0-9][0-9]*/v${LATEST_VERSION}/g" "$file"
    else
        echo "Warning: $file not found."
    fi
done

echo "Update complete."
