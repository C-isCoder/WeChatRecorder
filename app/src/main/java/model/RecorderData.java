package model;

/**
 * Created by iscod on 2016/3/8.
 */
public class RecorderData {
    public float time;
    public String filePath;

    public RecorderData(float time, String filePath) {
        this.time = time;
        this.filePath = filePath;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
