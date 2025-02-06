package com.hubis.acs.common.handler;

import com.hubis.acs.common.constants.BaseConstants;
import jakarta.annotation.PostConstruct;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("BaseWorkClassLoader")
public class BaseWorkClassLoader {

    private final Logger logger = LoggerFactory.getLogger(BaseWorkClassLoader.class);

    protected static Map<String, Map<String, String>> lstWorkClass = new TreeMap<String, Map<String, String>>(String.CASE_INSENSITIVE_ORDER);

    @PostConstruct
    public void initialize()
    {
        try
        {
            initWorkClass();

            logger.info("BaseWorkClassLoader Initialized");
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());

            logger.error("BaseWorkClassLoader Load Error: " + e.getMessage());
        }
    }

    private void initWorkClass() throws Exception
    {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage(BaseConstants.SYSTEM.CONFIG.PACKAGE.GeneralWork)) // 해당 패키지의 클래스 경로 로드
                        .setScanners(new SubTypesScanner(true)) // 하위 패키지까지 포함
        );

        Set<Class<? extends GlobalWorkHandlerIF>> subTypes = reflections.getSubTypesOf(GlobalWorkHandlerIF.class);
        // 서브 클래스가 0개면 직접 출력
        if (subTypes.isEmpty()) {
            System.out.println("⚠️ No subclasses found. Check classpath or package structure.");
            return; // 더 이상 진행할 필요 없음
        }
        System.out.println("🔍 Found " + subTypes.size() + " subclasses of " + GlobalWorkHandlerIF.class.getName() + " in package: " + BaseConstants.SYSTEM.CONFIG.PACKAGE.GeneralWork);

        Iterator<?> it = reflections.getSubTypesOf(GlobalWorkHandlerIF.class).iterator();

        while(it.hasNext())
        {
            Object obj = it.next();

            String objName = obj.toString().substring(6);
            //String pkgName = objName.substring(0, objName.lastIndexOf("."));
            String grpName = "work";
            String clsName = objName.substring(objName.lastIndexOf(".")+1);

            initWorkClass(objName, grpName, clsName.toLowerCase());
        }
    }

    public void initWorkClass(Class<?> baseWorkClass, String workPackage) throws Exception
    {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage(workPackage)) // 해당 패키지의 클래스 경로 로드
                        .setScanners(new SubTypesScanner(true)) // 하위 패키지까지 포함
        );

        Set<Class<?>> subTypes = (Set<Class<?>>) reflections.getSubTypesOf(baseWorkClass);
        // 서브 클래스가 0개면 직접 출력
        if (subTypes.isEmpty()) {
            System.out.println("⚠️ No subclasses found. Check classpath or package structure.");
            return; // 더 이상 진행할 필요 없음
        }
        System.out.println("🔍 Found " + subTypes.size() + " subclasses of " + baseWorkClass.getName() + " in package: " + workPackage);

        Iterator<?> it = reflections.getSubTypesOf(baseWorkClass).iterator();
        while(it.hasNext())
        {
            Object obj = it.next();

            String objName = obj.toString().substring(6);
            String pkgName = objName.substring(0, objName.lastIndexOf("."));
            String grpName = pkgName.substring(pkgName.lastIndexOf(".")+1);
            String clsName = objName.substring(objName.lastIndexOf(".")+1);

            initWorkClass(objName, grpName, clsName.toLowerCase());
        }
    }

    private void initWorkClass(String objName, String grpName, String clsName) throws Exception
    {
        if (!lstWorkClass.containsKey(grpName))
            lstWorkClass.put(grpName, new HashMap<String, String>());

        lstWorkClass.get(grpName).put(clsName, objName);
    }

    private static Object lockObject = new Object();

    public static GlobalWorkHandlerIF getWorkObject(String workGroupId, String workId)
    {
        try
        {
            synchronized (lockObject)
            {
                String objName = lstWorkClass.get(workGroupId).get(workId);
                return (GlobalWorkHandlerIF) (Class.forName(objName).getConstructors()[0].newInstance());
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }


        return null;
    }
}

