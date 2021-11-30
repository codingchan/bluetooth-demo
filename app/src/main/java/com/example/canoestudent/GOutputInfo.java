package com.example.canoestudent;

import java.io.Serializable;

public class GOutputInfo implements Serializable {
    //加速度
    private float accel_x;
    private float accel_y;
    private float accel_z;

    //角速度
    private float angle_x;
    private float angle_y;
    private float angle_z;

    //磁场归一化值//
    private float mag_x;
    private float mag_y;
    private float mag_z;

    //磁场强度
    private float raw_mag_x;
    private float raw_mag_y;
    private float raw_mag_z;

    //欧拉角
    private float pitch;
    private float roll;
    private float yaw;

    //四元数
    private float quaternion_data0;
    private float quaternion_data1;
    private float quaternion_data2;
    private float quaternion_data3;

    private int timeid;

    public int getTimeid() {
        return timeid;
    }

    public void setTimeid(int timeid) {
        this.timeid = timeid;
    }

    public float getAccel_x() {
        return accel_x;
    }

    public void setAccel_x(float accel_x) {
        this.accel_x = accel_x;
    }

    public float getAccel_y() {
        return accel_y;
    }

    public void setAccel_y(float accel_y) {
        this.accel_y = accel_y;
    }

    public float getAccel_z() {
        return accel_z;
    }

    public void setAccel_z(float accel_z) {
        this.accel_z = accel_z;
    }

    public float getAngle_x() {
        return angle_x;
    }

    public void setAngle_x(float angle_x) {
        this.angle_x = angle_x;
    }

    public float getAngle_y() {
        return angle_y;
    }

    public void setAngle_y(float angle_y) {
        this.angle_y = angle_y;
    }

    public float getAngle_z() {
        return angle_z;
    }

    public void setAngle_z(float angle_z) {
        this.angle_z = angle_z;
    }

    public float getMag_x() {
        return mag_x;
    }

    public void setMag_x(float mag_x) {
        this.mag_x = mag_x;
    }

    public float getMag_y() {
        return mag_y;
    }

    public void setMag_y(float mag_y) {
        this.mag_y = mag_y;
    }

    public float getMag_z() {
        return mag_z;
    }

    public void setMag_z(float mag_z) {
        this.mag_z = mag_z;
    }

    public float getRaw_mag_x() {
        return raw_mag_x;
    }

    public void setRaw_mag_x(float raw_mag_x) {
        this.raw_mag_x = raw_mag_x;
    }

    public float getRaw_mag_y() {
        return raw_mag_y;
    }

    public void setRaw_mag_y(float raw_mag_y) {
        this.raw_mag_y = raw_mag_y;
    }

    public float getRaw_mag_z() {
        return raw_mag_z;
    }

    public void setRaw_mag_z(float raw_mag_z) {
        this.raw_mag_z = raw_mag_z;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getQuaternion_data0() {
        return quaternion_data0;
    }

    public void setQuaternion_data0(float quaternion_data0) {
        this.quaternion_data0 = quaternion_data0;
    }

    public float getQuaternion_data1() {
        return quaternion_data1;
    }

    public void setQuaternion_data1(float quaternion_data1) {
        this.quaternion_data1 = quaternion_data1;
    }

    public float getQuaternion_data2() {
        return quaternion_data2;
    }

    public void setQuaternion_data2(float quaternion_data2) {
        this.quaternion_data2 = quaternion_data2;
    }

    public float getQuaternion_data3() {
        return quaternion_data3;
    }

    public void setQuaternion_data3(float quaternion_data3) {
        this.quaternion_data3 = quaternion_data3;
    }
}
