package com.bbkj.controller;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import vi.SnAndTime;

import javax.print.PrintException;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author rrx
 */
@Controller
@RequestMapping(value = "/get")
public class DrawingController {
    @RequestMapping(value = "/img",
            consumes = "application/json",
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public ImgUrl getImg(@RequestBody SnAndTime snAndTime) {
        try {
            System.setProperty("java.awt.headless", "true");
            ImgUrl imgUrl = new ImgUrl();
            Calendar now = Calendar.getInstance();
            OkHttpClient client = new OkHttpClient();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            String a = snAndTime.getSn();
            System.out.println(a);
            System.out.println(snAndTime.getStart());
            System.out.println(snAndTime.getEnd());
            String sn = "BB0000" + a + "00000000";
            String start = snAndTime.getStart();
            String end = snAndTime.getEnd();
            File file1 = new File("./src/main/webapp/img/" + sn + start + end + ".png");
            if (file1.exists()) {
                imgUrl.setImgUrl("./img/" + sn + start + end + ".png");
                return imgUrl;
            }
            int year = now.get(Calendar.YEAR);
            int month = now.get(Calendar.MONTH) + 1;
            int day = now.get(Calendar.DAY_OF_MONTH);
            int h = now.get(Calendar.HOUR_OF_DAY);
            //自动补全时间
            if (start.split("-").length == 3) {
                start = year + "-" + month + "-" + start;
            }
            if (end.split("-").length == 3) {
                end = year + "-" + month + "-" + end;
            }
            if (start.split("-").length == 2) {
                start = year + "-" + month + "-" + day + "-" + start;
            }
            if (end.split("-").length == 2) {
                end = year + "-" + month + "-" + day + "-" + end;
            }
            if (start.split("-").length == 1) {
                start = year + "-" + month + "-" + day + "-" + h + "-" + start;
            }
            if (end.split("-").length == 1) {
                end = year + "-" + month + "-" + day + "-" + h + "-" + end;
            }
            long st = sdf.parse(start).getTime();
            long en = sdf.parse(end).getTime();
            //获取原始数据
            String url = "http://data.91ganlu.com/data/download?sn=" + sn + "&start=" + st + "&end=" + en;
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Cache-Control", "no-cache")
                    .build();

            String file = "./File/" + sn + ".log";
            File dataFile = new File(file);
            Response response = client.newCall(request).execute();
            String data = response.body().string();
            //noData
            if (data.length() < 30) {
                imgUrl.setImgUrl("./nodata/nodata.png");
                return imgUrl;
            }
            OutputStreamWriter pw = new OutputStreamWriter(new FileOutputStream(dataFile));
            pw.write(data);
            pw.flush();
            pw.close();
            JFrame frame = new JFrame("原始波形图");
            frame.setLayout(new GridLayout(1, 1, 1, 1));
            String imgOutFile = "./src/main/webapp/img/" + sn + start + end + ".png";
            frame.add(new Ht(file, imgOutFile).getChartPanel());
            frame.setBounds(50, 50, 1200, 800);
            frame.setVisible(true);
            frame.dispose();
            imgUrl.setImgUrl("./img/" + sn + start + end + ".png");
            return imgUrl;
        } catch (Exception e) {

            ImgUrl imgUrl = new ImgUrl();
            imgUrl.setImgUrl("./nodata/nodata.png");
            return imgUrl;
        }
    }
}
