import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/*Takes a jar file as command line argument and prints out a listing of all the classes inside, as well as the number of declared public, private, protected, and static methods for each, and the number of declared fields for each.*/
public class JarClasses{
  // List classes inside the JAR
  public static Set<String> getClasses(File jarFile){
    Set<String> classNames = new TreeSet<>(); // Use TreeSet to sort the class names
    try (JarFile jar = new JarFile(jarFile)){
      Enumeration<JarEntry> entries = jar.entries();
      while (entries.hasMoreElements()){
        JarEntry entry = entries.nextElement();
        if (entry.getName().endsWith(".class")){
          String className = entry.getName().replace("/", ".").replace(".class", "");
          classNames.add(className);
        }
      } 
    }
    catch(IOException e){
      System.out.println("Error: " + e.getMessage());
    }
    return classNames;
  }

  // Print out the class name and its methods
  public static void printClassMethods(Class<?> classes){
    Method[] methods = classes.getDeclaredMethods();
    Field[] fields = classes.getDeclaredFields();
    int publicCount = 0;
    int privateCount = 0;
    int protectedCount = 0;
    int staticCount = 0;

    for (Method method : methods){
      int modifiers = method.getModifiers();
      if (Modifier.isPublic(modifiers)) publicCount++;
      else if (Modifier.isPrivate(modifiers)) privateCount++;
      else if (Modifier.isProtected(modifiers)) protectedCount++;
      else if (Modifier.isStatic(modifiers)) staticCount++;
    }

    int fieldCount = fields.length;
    System.out.println("----------" + classes.getName() + "----------");
    System.out.println("  Public methods: " + publicCount);
    System.out.println("  Private methods: " + privateCount);
    System.out.println("  Protected methods: " + protectedCount);
    System.out.println("  Static methods: " + staticCount);
    System.out.println("  Fields: " + fieldCount);
  }

  /* =================== Main function ===================== */
  public static void main(String[] args) {
    // Check edge cases
    if (args.length != 1){
      System.out.println("Usage: java JarClasses <jar file>");
      return;
    }
    if (!args[0].endsWith(".jar")){
      System.out.println("Error: " + args[0] + " is not a jar file");
      return;
    }
    if (!new File(args[0]).exists()){
      System.out.println("Error: " + args[0] + " does not exist");
      return;
    }
    
    File jarFile = new File(args[0]);
    Set<String> classNames = getClasses(jarFile);
    try (URLClassLoader classLoader = new URLClassLoader(
      new URL[]{jarFile.toURI().toURL()},
      JarClasses.class.getClassLoader())) {

    for (String className : classNames) {
      try {
          Class<?> classes = Class.forName(className, false, classLoader);
          printClassMethods(classes);
      } catch (Throwable t) {
          // Skip classes that cannot be loaded
          System.out.println("Cannot load " + className + ": " + t.getMessage());
      }
    }
    } catch (IOException e) {
    System.out.println("Error loading JAR: " + e.getMessage());
    }
  }
}