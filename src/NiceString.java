import java.io.*;
import java.util.*;

public class NiceString{

  public String s;
  public HashMap<String, Integer> map;
  NiceString(String s){
    this.s = s;
    map = new HashMap<String, Integer>();
    map.put("ni", 0);
    map.put("ne", 2);
    map.put("nc", 2);
    map.put("ce", 0);
    map.put("cn", 2);
    map.put("ci", 2);
    map.put("in", 2);
    map.put("ie", 2);
    map.put("ic", 2);
    map.put("ei", 2);
    map.put("ec", 2);
    map.put("en", 2);
  }

  public static void main(String[] args){
    String s = "neinceceincic";
    NiceString ns = new NiceString(s);
    System.out.println(ns.s);
    System.out.println("Min chars to remove: " + ns.longest_nice_substring(ns.s));
  }

  public int longest_nice_substring(String s){

  	if(s == null || s.length() == 0){
  		return 0;
  	}

    return min_char_to_remove(s);
  }

  public HashMap<String, Integer> get_map(){
    return this.map;
  }

  public void add_to_map(String s, int i){
    this.map.put(s, i);
  }

  public int min_char_to_remove(String s){
    if(s == null || s.length() == 0){
      return 0;
    }
    if(s.length() == 1){
      return 1;
    }
    if (this.get_map().get(s) != null){
      //System.out.println("String: " + s + ", count: " + this.get_map().get(s));
      return this.get_map().get(s);
    }

    int count = 0;
    int start, end, j;

    for(start = 0; start < s.length() - 1;){
      // check for first characters of string s. if they are 'i' or 'e', increment count and shrink string s
      if(s.charAt(start) == 'i' || s.charAt(start) == 'e'){
        start++;
        count++;
      } else{
        break;
      }
    }

    //System.out.println("start: " + start);
    //System.out.println(count);

    for(end = s.length() - 1; end > start;){
      // check for last characters of string s. if they are 'n' or 'c', increment count and shrink string s
      if(s.charAt(end) == 'n' || s.charAt(end) == 'c'){
        end--;
        count++;
      } else{
        break;
      }
    }

    //System.out.println("end: " + end);
    //System.out.println(count);

    char pair;
    if(s.charAt(start) == 'n')
      pair = 'i';
    else
      pair = 'e';

    ArrayList<Integer> counts = new ArrayList<Integer>();
    for(j = end; j > start; j--){

      if(s.charAt(j) == pair){
        //System.out.println("j: " + j);
        //System.out.println(s.substring(start+1, j));
        //System.out.println(s.substring(j+1, end+1));
        counts.add(count + min_char_to_remove(s.substring(start+1, j)) + min_char_to_remove(s.substring(j+1, end+1)));
      }

    }
    if(j == start && counts.size() == 0){
      counts.add(count + end - start + 1);
    }

    //System.out.println("start: " + start + ", end: " + end + ", count: " + count);
    Collections.sort(counts);
    this.map.put(s, counts.get(0));
    System.out.println("String: " + s + ", count: " + counts.get(0));
    return counts.get(0);
  }

}
