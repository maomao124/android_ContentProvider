package mao.android_contentprovider.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import mao.android_contentprovider.dao.StudentContent;
import mao.android_contentprovider.dao.StudentDao;

public class StudentProvider extends ContentProvider
{
    /*
       需要继承ContentProvider
     */

    /**
     * 标签
     */
    private static final String TAG = "StudentProvider";


    private StudentDao studentDao;

    /**
     * Uri匹配时的代号
     */
    public static final int STUDENT_INFO = 1;

    /**
     * uri匹配器
     */
    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static
    {
        //往Uri匹配器中添加指定的数据路径
        uriMatcher.addURI(StudentContent.AUTHORITIES, StudentContent.path, STUDENT_INFO);
    }


    public StudentProvider()
    {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        int count = 0;
        if (uriMatcher.match(uri) == STUDENT_INFO)
        {
            //匹配到了学生信息表
            //获取SQLite数据库的写连接
            SQLiteDatabase writableDatabase = studentDao.getWritableDatabase();
            //执行SQLite的删除操作，并返回删除记录的数目
            count = writableDatabase.delete(StudentContent.TABLE_NAME, selection, selectionArgs);
            // 关闭SQLite数据库连接
            //writableDatabase.close();
        }
        return count;

    }

    @Override
    public String getType(Uri uri)
    {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        if (uriMatcher.match(uri) == STUDENT_INFO)
        {
            // 匹配到了学生信息表
            // 获取SQLite数据库的写连接
            SQLiteDatabase writableDatabase = studentDao.getWritableDatabase();
            // 向指定的表插入数据，返回记录的行号
            long rowId = writableDatabase.insert(StudentContent.TABLE_NAME, null, values);
            // 判断插入是否执行成功
            if (rowId > 0)
            {
                // 如果添加成功，就利用新记录的行号生成新的地址
                Uri newUri = ContentUris.withAppendedId(StudentContent.CONTENT_URI, rowId);
                //通知监听器，数据已经改变
                getContext().getContentResolver().notifyChange(newUri, null);
            }
            // 关闭SQLite数据库连接
            //writableDatabase.close();
        }
        return uri;
    }

    @Override
    public boolean onCreate()
    {
        studentDao = StudentDao.getInstance(getContext());
        studentDao.openWriteConnection();
        studentDao.openWriteConnection();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder)
    {
        Cursor cursor = null;
        if (uriMatcher.match(uri) == STUDENT_INFO)
        {
            //匹配到了学生信息表
            //获取SQLite数据库的读连接
            SQLiteDatabase readableDatabase = studentDao.getReadableDatabase();
            //执行SQLite的查询操作
            cursor = readableDatabase.query(StudentContent.TABLE_NAME, projection,
                    selection, selectionArgs, null, null,
                    sortOrder);
            //设置内容解析器的监听
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        //返回查询结果集的游标
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs)
    {
        int count = 0;
        if (uriMatcher.match(uri) == STUDENT_INFO)
        {
            // 匹配到了学生信息表
            // 获取SQLite数据库的写连接
            SQLiteDatabase writableDatabase = studentDao.getWritableDatabase();
            // 向指定的表插入数据，返回记录的行号
            count = writableDatabase.update(StudentContent.TABLE_NAME, values, selection, selectionArgs);
            // 关闭SQLite数据库连接
            //writableDatabase.close();
        }
        return count;
    }
}