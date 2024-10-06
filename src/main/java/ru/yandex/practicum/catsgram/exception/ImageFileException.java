package ru.yandex.practicum.catsgram.exception;

import java.io.IOException;

public class ImageFileException extends Throwable {
    public ImageFileException(String s, IOException e) {
    }

    public ImageFileException(String s) {
    }
}
