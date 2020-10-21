

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HelloXlassLoader extends ClassLoader{

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, NoSuchMethodException, InvocationTargetException {
        HelloXlassLoader helloXlassLoader = new HelloXlassLoader();
        Class<?> clazz = helloXlassLoader.findClass("Hello");
        Method method =  clazz.getMethod("hello");
        Object hello = clazz.newInstance();
        method.invoke(hello);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        File file = new File(name + ".xlass");
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             FileInputStream fis = new FileInputStream(file);) {

            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer, 0, 1024)) != -1) {
                bos.write(buffer, 0, len);
            }
            byte[] bytes = bos.toByteArray();
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) (255 - bytes[i]);
            }
            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ClassNotFoundException(name + ".xlass" + " not found");
        }
    }
}
