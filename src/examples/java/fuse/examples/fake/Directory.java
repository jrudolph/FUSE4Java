package fuse.examples.fake;

import java.util.LinkedHashMap;
import java.util.Map;

class Directory extends Node
{
   private final Map<String,Node> files = new LinkedHashMap<String,Node>();

   Directory(String name, int mode, String ... xattrs)
   {
      super(name, mode, xattrs);
   }

   void add(Node n)
   {
      files.put(n.getName(), n);
   }

   public String toString()
   {
      return super.toString() + " with " + files.size() + " files";
   }
   
   public Map<String,Node> getFiles() {
	   return files;
   }
}
