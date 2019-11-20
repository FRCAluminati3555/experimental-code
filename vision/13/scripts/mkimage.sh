#!/bin/bash

DEVICE=$1
FILE=$2

echo "Usage: ./mkimage.sh <device_name> <image_name>"

chmod +x ./pishrink/pishrink.sh

echo "Creating image..."
dd of=$FILE if=$DEVICE

echo "Shrinking image..."
./pishrink/pishrink.sh $FILE
