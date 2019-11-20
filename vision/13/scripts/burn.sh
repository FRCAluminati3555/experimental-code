#!/bin/bash

FILE=$1
DEVICE=$2

echo "Usage: ./burn.sh <image_name> <device_name>"

echo "Burning..."
dd if=$FILE of=$DEVICE
