package com.markov.filescopy;

/**
 * Created by Markov on 19.02.2017.
 */
public class FileParameters {
    private final String fileName;
    private final String absolutePath;
    private final long size;

    public FileParameters(String fileName, String absolutePath, long size) {
        this.fileName = fileName;
        this.absolutePath = absolutePath;
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public long getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileParameters that = (FileParameters) o;

        if (size != that.size) return false;
        return fileName.equals(that.fileName);
    }

    @Override
    public int hashCode() {
        int result = fileName.hashCode();
        result = 31 * result + (int) (size ^ (size >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "FileParameters{" +
                "fileName='" + fileName + '\'' +
                ", size=" + size / 1000000 + "MB" +
                '}';
    }
}
