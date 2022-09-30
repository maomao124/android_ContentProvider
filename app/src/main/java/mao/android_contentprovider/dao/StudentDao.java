package mao.android_contentprovider.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import mao.android_contentprovider.entity.Student;

/**
 * Project name(项目名称)：android_ContentProvider
 * Package(包名): mao.android_contentprovider.dao
 * Class(类名): StudentDao
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/9/30
 * Time(创建时间)： 15:18
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class StudentDao extends SQLiteOpenHelper
{
    /**
     * 数据库名字
     */
    private static final String DB_NAME = "student.db";

    /**
     * 表名
     */
    public static final String TABLE_NAME = "student";

    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 1;

    /**
     * 实例，单例模式，懒汉式，双重检查锁方式
     */
    private static volatile StudentDao studentDao = null;

    /**
     * 读数据库
     */
    private SQLiteDatabase readDatabase;
    /**
     * 写数据库
     */
    private SQLiteDatabase writeDatabase;

    /**
     * 标签
     */
    private static final String TAG = "StudentDao";


    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public StudentDao(@Nullable Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 获得实例
     *
     * @param context 上下文
     * @return {@link StudentDao}
     */
    public static StudentDao getInstance(Context context)
    {
        if (studentDao == null)
        {
            synchronized (StudentDao.class)
            {
                if (studentDao == null)
                {
                    studentDao = new StudentDao(context);
                }
            }
        }
        return studentDao;
    }

    /**
     * 打开读连接
     *
     * @return {@link SQLiteDatabase}
     */
    public SQLiteDatabase openReadConnection()
    {
        if (readDatabase == null || !readDatabase.isOpen())
        {
            readDatabase = studentDao.getReadableDatabase();
        }
        return readDatabase;
    }

    /**
     * 打开写连接
     *
     * @return {@link SQLiteDatabase}
     */
    public SQLiteDatabase openWriteConnection()
    {
        if (writeDatabase == null || !writeDatabase.isOpen())
        {
            writeDatabase = studentDao.getWritableDatabase();
        }
        return readDatabase;
    }

    /**
     * 关闭数据库读连接和写连接
     */
    public void closeConnection()
    {
        if (readDatabase != null && readDatabase.isOpen())
        {
            readDatabase.close();
            readDatabase = null;
        }

        if (writeDatabase != null && writeDatabase.isOpen())
        {
            writeDatabase.close();
            writeDatabase = null;
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {

        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " name VARCHAR NOT NULL," +
                " age INTEGER NOT NULL," +
                " weight FLOAT NOT NULL)";
        db.execSQL(sql);
    }

    /**
     * 数据库版本更新时触发回调
     *
     * @param db         SQLiteDatabase
     * @param oldVersion 旧版本
     * @param newVersion 新版本
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }


    /**
     * 查询所有
     *
     * @return {@link List}<{@link Student}>
     */
    public List<Student> queryAll()
    {
        List<Student> list = new ArrayList<>();

        Cursor cursor = readDatabase.query(TABLE_NAME, null, "1=1", new String[]{}, null, null, null);

        while (cursor.moveToNext())
        {
            Student student = new Student();
            setStudent(cursor, student);
            list.add(student);
        }

        cursor.close();
        return list;
    }


    /**
     * 通过id(主键)查询
     *
     * @param id id(主键)
     * @return {@link Student}
     */
    public Student queryById(Serializable id)
    {
        Student student = null;
        Cursor cursor = readDatabase.query(TABLE_NAME, null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToNext())
        {
            student = new Student();
            setStudent(cursor, student);
        }
        cursor.close();
        return student;
    }


    /**
     * 插入一条数据
     *
     * @param student Student对象
     * @return boolean
     */
    public boolean insert(Student student)
    {
        ContentValues contentValues = new ContentValues();
        setContentValues(student, contentValues);
        long insert = writeDatabase.insert(TABLE_NAME, null, contentValues);
        return insert > 0;
    }

    /**
     * 插入多条数据
     *
     * @param list 列表
     * @return boolean
     */
    public boolean insert(List<Student> list)
    {
        try
        {
            writeDatabase.beginTransaction();
            for (Student student : list)
            {
                boolean insert = this.insert(student);
                if (!insert)
                {
                    throw new Exception();
                }
            }
            writeDatabase.setTransactionSuccessful();
            return true;
        }
        catch (Exception e)
        {
            writeDatabase.endTransaction();
            Log.e(TAG, "insert: ", e);
            return false;
        }
    }

    /**
     * 更新
     *
     * @param student Student对象
     * @return boolean
     */
    public boolean update(Student student)
    {
        ContentValues contentValues = new ContentValues();
        setContentValues(student, contentValues);
        int update = writeDatabase.update(TABLE_NAME, contentValues, "id=?", new String[]{student.getId().toString()});
        return update > 0;
    }

    /**
     * 插入或更新，先尝试插入，如果插入失败，更新
     *
     * @param student Student对象
     * @return boolean
     */
    public boolean insertOrUpdate(Student student)
    {
        boolean insert = insert(student);
        if (insert)
        {
            return true;
        }
        return update(student);
    }

    /**
     * 删除
     *
     * @param id id
     * @return boolean
     */
    public boolean delete(Serializable id)
    {
        int delete = writeDatabase.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
        return delete > 0;
    }


    /**
     * 填充ContentValues
     *
     * @param student       Student
     * @param contentValues ContentValues
     */
    private void setContentValues(Student student, ContentValues contentValues)
    {
        contentValues.put("id", student.getId());
        contentValues.put("name", student.getName());
        contentValues.put("age", student.getAge());
        contentValues.put("weight", student.getWeight());
    }

    /**
     * 填充Student
     *
     * @param cursor  游标
     * @param student Student对象
     */
    private Student setStudent(Cursor cursor, Student student)
    {
        student.setId(cursor.getLong(0));
        student.setName(cursor.getString(1));
        student.setAge(cursor.getInt(2));
        student.setWeight(cursor.getFloat(3));

        return student;
    }


}
