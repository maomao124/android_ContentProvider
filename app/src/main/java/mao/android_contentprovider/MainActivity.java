package mao.android_contentprovider;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import mao.android_contentprovider.dao.StudentDao;

public class MainActivity extends AppCompatActivity
{

    private StudentDao studentDao;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        studentDao = StudentDao.getInstance(this);
        studentDao.openReadConnection();
        studentDao.openWriteConnection();
        Log.d(TAG, "onCreate: ");

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d(TAG, "onStart: query:\n" + studentDao.queryAll() + "\n");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        studentDao.closeConnection();

    }
}