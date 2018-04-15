#!/bin/bash
kill $(ps aux | grep 'firefox' | awk '{print $2}')