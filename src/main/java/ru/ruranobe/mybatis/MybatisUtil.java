package ru.ruranobe.mybatis;

import java.io.IOException;
import java.io.Reader;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MybatisUtil 
{
    public static SqlSessionFactory getSessionFactory() 
    {
        return sessionFactory;
    }
    
    private static SqlSessionFactory buildSessionFactory() 
    {
        Reader reader = null;
	try 
        {
            reader = Resources.getResourceAsReader("mybatis-config.xml");
	} 
        catch (IOException ex) 
        {
            System.err.println("Initial SessionFactory creation failed." + ex);
	    throw new ExceptionInInitializerError(ex);
	}
        return new SqlSessionFactoryBuilder().build(reader);
    } 
  
    private static final SqlSessionFactory sessionFactory = buildSessionFactory();
}
