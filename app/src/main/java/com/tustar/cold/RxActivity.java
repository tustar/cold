package com.tustar.cold;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class RxActivity extends Activity {

    private static final String tag = "RxActivity";
    private Context context;
    private ImageView mRxImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);
        context = this;

        new Button(this).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mRxImageView = findViewById(R.id.rx_image);

//        from();
        loadImage(R.drawable.icon);
        loadImage(-1);
//        flatMap();
//        throttleFirst();
        lift();

    }

    public void lift() {
        Observable.just(1, 2, 3).lift(new Observable.Operator<String, Integer>() {
            @Override
            public Subscriber<? super Integer> call(final Subscriber<? super String> subscriber) {
                // 将事件序列中的 Integer 对象转换为 String 对象
                return new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        subscriber.onNext("I am " + integer);
                    }
                };
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.d(tag, s);
            }
        });
    }

//    public <R> Observable<R> lift(Operator<? extends R, ? super T> operator) {
//        return Observable.create(new Observable.OnSubscribe<R>() {
//            @Override
//            public void call(Subscriber subscriber) {
//                Subscriber newSubscriber = operator.call(subscriber);
//                newSubscriber.onStart();
//                onSubscribe.call(newSubscriber);
//            }
//        });
//    }

    private void throttleFirst() {
        Observable.create(new Observable.OnSubscribe<Integer>() {

            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(1);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                subscriber.onNext(2);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                subscriber.onNext(3);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                subscriber.onNext(4);
                subscriber.onCompleted();
            }
        })
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer aInt) {
                        Log.d(tag, aInt.toString());
                    }
                });
    }

    private void flatMap() {
        final List<Student> students = generateStudents();
        Subscriber<Course> subscriber = new Subscriber<Course>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Course course) {
                Log.d(tag, course.getName());
            }
        };
        Observable.from(students)
                .flatMap(new Func1<Student, Observable<Course>>() {
                    @Override
                    public Observable<Course> call(Student student) {
                        return Observable.from(student.getCourses());
                    }
                }).subscribe(subscriber);
    }

    private void mapStudentName() {
        List<Student> students = generateStudents();
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String name) {
                Log.d(tag, name);
            }
        };
        Observable.from(students).map(new Func1<Student, String>() {
            @Override
            public String call(Student student) {
                return student.getName();
            }
        }).subscribe(subscriber);
    }

    private List<Student> generateStudents() {
        List<Student> students = new ArrayList<>();
        students.add(new Student(1, "ZhangSan", generateCourses()));
        students.add(new Student(2, "LiSi", generateCourses()));
        students.add(new Student(3, "ZhaoWu", generateCourses()));
        students.add(new Student(4, "QianLiu", generateCourses()));
        students.add(new Student(5, "LiuQi", generateCourses()));
        return students;
    }

    private List<Course> generateCourses() {
        List<Course> courses = new ArrayList<>();
        courses.add(new Course(1, "English"));
        courses.add(new Course(2, "Math"));
        courses.add(new Course(3, "Java"));
        courses.add(new Course(4, "C++"));
        courses.add(new Course(5, "Python"));
        courses.add(new Course(6, "Kotlin"));
        int toIndex = new Random().nextInt(courses.size());
        return courses.subList(0, toIndex);
    }

    private void map() {
        Observable.just("/images/logo") // 输入类型 String
                .map(new Func1<String, Bitmap>() {
                         @Override
                         public Bitmap call(String filePath) {// 参数类型 String
                             return getBitmapFromPath(filePath); // 返回类型 Bitmap
                         }
                     }
                )
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) { // 参数类型 Bitmap
                        showBitmap(bitmap);
                    }
                });
    }

    private Bitmap getBitmapFromPath(String filePath) {
        return null;
    }

    private void showBitmap(Bitmap bitmap) {
    }

    private void loadImage(final int resId) {
        Observable.create(new Observable.OnSubscribe<Drawable>() {
            @Override
            public void call(Subscriber<? super Drawable> subscriber) {
                Drawable drawable = getTheme().getDrawable(resId);
                subscriber.onNext(drawable);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<Drawable>() {


                    @Override
                    public void onNext(Drawable drawable) {
                        mRxImageView.setImageDrawable(drawable);
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void sample() {
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onNext(String s) {
                Log.d(tag, "Item: " + s);
            }

            @Override
            public void onCompleted() {
                Log.d(tag, "Completed!");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(tag, "Error!");
            }
        };
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onNext(String s) {
                Log.d(tag, "Item: " + s);
            }

            @Override
            public void onCompleted() {
                Log.d(tag, "Completed!");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(tag, "Error!");
            }
        };
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("Hello");
                subscriber.onNext("Hi");
                subscriber.onNext("Aloha");
                subscriber.onCompleted();
            }
        });
        observable.subscribe(observer);
        observable.subscribe(subscriber);

        Action1<String> onNextAction = new Action1<String>() {
            // onNext()
            @Override
            public void call(String s) {
                Log.d(tag, s);
            }
        };
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                // Error handling
            }
        };
        Action0 onCompletedAction = new Action0() {
            // onCompleted()
            @Override
            public void call() {
                Log.d(tag, "completed");
            }
        };

        // 自动创建 Subscriber ，并使用 onNextAction 来定义 onNext()
        observable.subscribe(onNextAction);
        // 自动创建 Subscriber ，并使用 onNextAction 和 onErrorAction 来定义 onNext() 和 onError()
        observable.subscribe(onNextAction, onErrorAction);
        // 自动创建 Subscriber ，并使用 onNextAction、 onErrorAction 和 onCompletedAction 来定义 onNext()、 onError() 和 onCompleted()
        observable.subscribe(onNextAction, onErrorAction, onCompletedAction);
    }

    private void from() {
        String[] names = {"John", "Tom", "Jerry"};
        Observable.from(names).subscribe(new Action1<String>() {
            @Override
            public void call(String name) {
                Log.d(tag, name);
            }
        });
    }
}