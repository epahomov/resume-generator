#!/bin/bash
kill $(ps aux | grep 'geckodriver_firefox' | awk '{print $2}')
kill $(ps aux | grep 'firefox-bin' | awk '{print $2}')