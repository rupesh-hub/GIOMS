package com.gerp.templating.services;

import com.gerp.templating.entity.TippaniDetail;
import com.google.zxing.WriterException;

import java.io.IOException;

public interface TippaniService {

    String getTippani(TippaniDetail tippaniDetail) throws IOException, WriterException;

    String getTippaniHeader(TippaniDetail tippaniDetail) throws IOException, WriterException;
}
