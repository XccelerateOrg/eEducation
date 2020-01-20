package io.agora.education.classroom.strategy.channel;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.agora.base.LogManager;
import io.agora.education.classroom.bean.channel.ChannelInfo;
import io.agora.education.classroom.bean.user.Student;
import io.agora.education.classroom.bean.user.Teacher;
import io.agora.rtm.ResultCallback;
import io.agora.sdk.listener.RtcEventListener;
import io.agora.sdk.manager.RtcManager;

public abstract class ChannelStrategy<T> {

    private final LogManager log = new LogManager(this.getClass().getName());

    private static final int SHARE_UID = 7;

    private String channelId;
    private ChannelInfo channelInfo;
    private List<Integer> rtcUsers;
    ChannelEventListener channelEventListener;

    ChannelStrategy(String channelId, Student local) {
        this.channelId = channelId;
        channelInfo = new ChannelInfo(local);
        rtcUsers = new ArrayList<>();
        RtcManager.instance().registerListener(rtcEventListener);
    }

    public String getChannelId() {
        return channelId;
    }

    public Student getLocal() {
        try {
            return channelInfo.local.clone();
        } catch (Exception e) {
            return new Student();
        }
    }

    void setLocal(Student local) {
        String json = local.toJsonString();
        if (getLocal().isGenerate == local.isGenerate && TextUtils.equals(json, getLocal().toJsonString())) {
            return;
        }
        log.d("setLocal %s", json);
        channelInfo.local = local;
        if (channelEventListener != null) {
            channelEventListener.onLocalChanged(getLocal());
        }
    }

    public Teacher getTeacher() {
        return channelInfo.teacher;
    }

    protected void setTeacher(Teacher teacher) {
        String json = teacher.toJsonString();
        if (TextUtils.equals(json, new Gson().toJson(getTeacher()))) {
            return;
        }
        log.d("setTeacher %s", json);
        channelInfo.teacher = teacher;
        if (channelEventListener != null) {
            channelEventListener.onTeacherChanged(getTeacher());
        }
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        if (!getLocal().isGenerate)
            students.add(0, getLocal());
        students.addAll(channelInfo.students);
        return students;
    }

    void setStudents(List<Student> students) {
        Gson gson = new Gson();
        String json = gson.toJson(students);
        if (TextUtils.equals(json, gson.toJson(channelInfo.students))) {
            return;
        }
        log.d("setUsers %s", json);
        channelInfo.students.clear();
        for (Student student : students) {
            if (rtcUsers.contains(student.uid)) {
                student.isRtcOnline = true;
            }
        }
        channelInfo.students.addAll(students);
        if (channelEventListener != null) {
            channelEventListener.onStudentsChanged(channelInfo.students);
        }
    }

    public void setChannelEventListener(ChannelEventListener listener) {
        channelEventListener = listener;
    }

    public void release() {
        channelEventListener = null;
        RtcManager.instance().unregisterListener(rtcEventListener);
    }

    public abstract void queryOnlineStudentNum(@NonNull ResultCallback<Integer> callback);

    public abstract void queryChannelInfo(@Nullable ResultCallback<Void> callback);

    public abstract void parseChannelInfo(T data);

    public abstract void updateLocalAttribute(Student local, @Nullable ResultCallback<Void> callback);

    public abstract void clearLocalAttribute(@Nullable ResultCallback<Void> callback);

    private RtcEventListener rtcEventListener = new RtcEventListener() {
        @Override
        public void onUserJoined(int uid, int elapsed) {
            if (uid == SHARE_UID) {
                if (channelEventListener != null) {
                    channelEventListener.onScreenShareJoined(uid);
                }
            } else {
                rtcUsers.add(uid);
            }
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            if (uid == SHARE_UID) {
                if (channelEventListener != null) {
                    channelEventListener.onScreenShareOffline(uid);
                }
            } else {
                rtcUsers.remove(Integer.valueOf(uid));
            }
        }
    };

}
