package com.router.protobuf;

import com.google.protobuf.CodedOutputStream;
import com.lupy.proto.ProtoAddress;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void test() {

        ProtoAddress.Address lupengyun = ProtoAddress.Address.newBuilder()
                .setName("lupengyun")
                .setAge(55)
                .setEmail("lupengyun2017@gmail.com")
                .setAddressType(ProtoAddress.Address.PhoneType.HOME)
                .build();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            lupengyun.writeTo(byteArrayOutputStream);

            String s = byteArrayOutputStream.toString(StandardCharsets.UTF_8.name());
            System.out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}