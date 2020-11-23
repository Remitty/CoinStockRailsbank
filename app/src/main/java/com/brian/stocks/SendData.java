package com.brian.stocks;

import android.util.Log;

import com.xuhao.didi.core.iocore.interfaces.ISendable;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;

public class SendData implements ISendable {
    protected String content = "";

    public SendData(String content){
        this.content = content;
    }

    @Override
    public final byte[] parse() {
        byte[] body = content.getBytes(Charset.defaultCharset());
        ByteBuffer bb = ByteBuffer.allocate(body.length);
        bb.order(ByteOrder.BIG_ENDIAN);
//        bb.put((byte)72);
//        bb.put((byte)84);
//        bb.put((byte)84);
//        bb.put((byte)80);
//        bb.put((byte)47);
//        bb.put((byte)49);
//        bb.putInt(body.length);
        bb.put(body);
        Log.d("socket header data1", Arrays.toString(bb.array()));
        return bb.array();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
