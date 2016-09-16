#!/bin/bash
process()
{
   cd $1;
   #loops through all files in current directory
   for element in * 
   do
   #for body
      if [ -f $element ] 
      then
      #actions if its file
      process_file $element;
      fi;
      if [ -d $element ] 
      then
      #actions if its directory   
      process_dir $element;
      fi;
   done
}
process_file()
{

echo "It is file $1";
}
process_dir()
{
echo "It is dir $1";
  if [ $1 = "target" ] 
  then
  rm -r target
  echo "Removed target directory"
  else
  (process $1)
  fi;
}

process $1