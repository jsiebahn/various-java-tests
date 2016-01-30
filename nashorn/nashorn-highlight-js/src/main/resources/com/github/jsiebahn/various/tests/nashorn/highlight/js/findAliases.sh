#!/usr/bin/env bash

aliasesProperties=""
for file in `find ./highlightJs/languages -type f`
do
  type=$file
  type=$(basename "$type")
  type="${type%.*}"
  aliasesProperties="$aliasesProperties
$type = $type"
  aliases=`cat $file | grep -e 'aliases[[:space:]]*:[[:space:]]*\['`
  aliases=`echo $aliases`
  if ! [ -z "$aliases" ]; then
    aliases=`echo "$aliases" | sed -E 's/[^[]*\[([^]]*)\].*/\1/g'`
    aliases=`echo "$aliases" | sed -E 's/,[[:space:]]*/ /g'`
    aliases=`echo "$aliases" | sed "s/'//g"`
    aliases=`echo "$aliases" | sed 's/"//g'`
    for alias in $aliases
    do
      echo "$type $alias"
      aliasesProperties="$aliasesProperties
$alias = $type"
    done
  fi
done

echo "$aliasesProperties" > ./aliases.properties

exit 0;