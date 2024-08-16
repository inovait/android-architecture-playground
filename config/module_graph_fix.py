#!/bin/python

## Script that fixes leaf module position on the graph
## See https://github.com/jraska/modules-graph-assert/issues/256

## To use, pipe output of the generateModulesGraphvizText into that script:
## ./gradlew generateModulesGraphvizText | config/module_graph_fix.py

import sys
import re

dep_regex = re.compile("\"([^\"]+)\" -> \"([^\"]+)\"")
has_children = {}

for line in sys.stdin:
    stripped_line = line.strip()
    match = dep_regex.search(stripped_line)
    if match is not None:
        parent = match[1]
        child = match[2]
        has_children[parent] = True
        if child not in has_children:
            has_children[child] = False
    if stripped_line != "}":
        print(stripped_line)
    
leaf_modules = [ f'\"{module}\"' for module in has_children if not has_children[module] ]
if len(leaf_modules) != 0:
    print("{ rank=max; " + ", ".join(leaf_modules) + " }")

print("}")