val a = List(1,2,3,4)

def walkthru(l: List[Int]): String = {
  if(l.isEmpty) ""
  else l.head.toString + " " + walkthru(l.tail)
}

walkthru(a)

