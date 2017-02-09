package ictsimulationpackage;

import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class Study {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		try {
//
//
//			Network list = new Network(102);
//			Building first = list.bldgList[0];
//			for (Link ln : list.allLinkList) {
//				ln.iniCap(1000, 1000);
//			}
//			System.out.println("put out");
//			System.out.println("bldg num:" + list.bldgNumNow);
//			// すべてのリストの確認
//			for (int i = 0; i < list.bldgNumNow; i++) {
//				System.out.println(i + ":" + list.bldgList[i].bname + "(" + list.bldgList[i].areaBldg.bname + ")"
//						+ "(bid:" + list.bldgList[i].bid + ")");
//			}
//			// 区外リンクの確認
//			Building bldg = list.bldgList[0];
//			try {
//				for (int i = 0; i < 7; i++) {
//					System.out.println(i + ":" + bldg.bname);
//					bldg = bldg.exBldgR;
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			System.out.println("ビル格納数" + list.bldgNumNow);
//			System.out.println("------------------------");
//			System.out.println("区内リンクの数:" + list.linkList.size());
//			System.out.println("区内中継リンクの数:" + list.exLinkList.size());
//			System.out.println("全リンクの数:" + list.allLinkList.size());
//
//			System.out.println("-----------練馬区内ループ-------------");
//			bldg = list.bldgList[0];
//			for(int i = 0 ; i < 40; i++){
//				System.out.print(bldg.bname + "=>");
//				bldg = bldg.bldgR;
//			}
//			System.out.println("");
//
//			System.out.println("-----------荏原区内ループ-------------");
//			bldg = list.bldgList[36];
//			for(int i = 0 ; i < 40; i++){
//				System.out.print(bldg.bname + "=>");
//				bldg = bldg.bldgR;
//			}
//			System.out.println("");
//
//			System.out.println("-----------荏原区内ループ-------------");
//			bldg = list.bldgList[71];
//			for(int i = 0 ; i < 40; i++){
//				System.out.print(bldg.bname + "=>");
//				bldg = bldg.bldgR;
//			}
//			System.out.println("");
//
//			System.out.println("-----------荏原区内ループ-------------");
//			bldg = list.bldgList[71];
//			for(int i = 0 ; i < 4; i++){
//				System.out.print(bldg.bname + "=>");
//				bldg = bldg.exBldgR;
//			}
//			System.out.println("");
//
//			System.out.println("-----------地区内探索-------------");
//			SmallRing sring = new SmallRing();
//			Building start = list.bldgList[36];
//			Building dest = list.bldgList[52];
//			ArrayList<Link> slist = sring.route(start, dest);
//			System.out.println("start:" + start.bname + "[" + start.bid + "]");
//			if (slist != null) {
//				for (Link ln : slist) {
//					System.out.println("->" + ln.id);
//				}
//			} else {
//				System.out.println("list is null!");
//			}
//			System.out.println("dest:" + dest.bname + "[" + dest.bid + "]");
//			System.out.println("-------------エリア外探索-----------");
//			start = list.bldgList[0];
//			dest = list.bldgList[40];
//			LargeRing lring = new LargeRing();
//			slist = lring.route(start, dest);
//			System.out.println("start:" + start.bname + "[" + start.bid + "]");
//			for (Link ln : slist) {
//				System.out.println("->" + ln.id);
//			}
//			System.out.println("dest:" + dest.bname + "[" + dest.bid + "]");
//			System.out.println("-------------区外エリア横断探索-----------");
//			LargeRing lRing = new LargeRing();
//			start = list.bldgList[102];
//			dest = list.bldgList[21];
//			slist = lRing.route(start, dest);
//			System.out.println("start:" + start.bname + "[" + start.bid + "]");
//			for (Link ln : slist) {
//				System.out.println("->" + ln.id);
//			}
//			System.out.println("dest:" + dest.bname + "[" + dest.bid + "]");
//			System.out.println("-------------ビル破壊エリア横断探索-----------");
//			// list.bldgList[39].makeBroken();
//			// list.bldgList[41].makeBroken();
//			// list.bldgList[42].linkR = null;
//			lRing = new LargeRing();
//			start = list.bldgList[102];
//			dest = list.bldgList[21];
//			slist = lRing.route(start, dest);
//			System.out.println("start:" + start.bname + "[" + start.bid + "]");
//			if (slist == null) {
//				System.out.println("no route has been found!");
//			} else {
//				for (Link ln : slist) {
//					System.out.println("->" + ln.id);
//				}
//			}
//			System.out.println("dest:" + dest.bname + "[" + dest.bid + "]");
//
//			// System.out.println("-------------ビル順序変更-----------");
//			// KosuDownloader d = new KosuDownloader();
//			// d.sortBldgList();
//			// int n = 0;
//			// for (Building bld : Network.bldgList) {
//			// System.out.println(n + " : " + bld.bname);
//			// n++;
//			// }
//			// System.out.println("-------------kosuDownloader-----------");
//			// d.download();
//			System.out.println("-------------Call test-----------");
//			Call.sumHoldTime[23] = 0;
//			Call call = new Call(start, dest, 23);
//			for (int i = 0; i < 100; i++) {
//				call = new Call(start, dest, 23);
//			}
//			System.out.println("start:" + start.bname + "[" + start.bid + "]");
//			for (Link ln : call.LinkList) {
//				System.out.println("->" + ln.id + "[cap:" + ln.capacity + "]");
//			}
//			System.out.println("dest:" + dest.bname + "[" + dest.bid + "]");
//			System.out.println(start.bname + " to " + dest.bname + ":" + call.success);
//
//			System.out.println("-------------Call test nerima to kugai-----------");
//			start = list.bldgList[0];
//			dest = list.bldgList[102];
//			call = new Call(start, dest, 23);
//			for (int i = 0; i < 100; i++) {
//				call = new Call(start, dest, 23);
//			}
//			System.out.println("start:" + start.bname + "[" + start.bid + "]");
//			if(call.success){
//				for (Link ln : call.LinkList) {
//					System.out.println("->" + ln.id + "[cap:" + ln.capacity + "]");
//				}
//			}
//
//			System.out.println("dest:" + dest.bname + "[" + dest.bid + "]");
//			System.out.println(start.bname + " to " + dest.bname + ":" + call.success);
//
//			System.out.println("-------------Call test occupied-----------");
//			list.findLink(55).capacity = list.findLink(55).iniCap;
//			list.findLink(21).capacity = list.findLink(21).iniCap;
//			call = new Call(start, dest, 23);
//			System.out.println("start:" + start.bname + "[" + start.bid + "]");
//			if (call.LinkList != null) {
//				for (Link ln : call.LinkList) {
//					System.out.println("->" + ln.id + "[" + ln.capacity + "]");
//				}
//			} else {
//				System.out.println("no route has been found");
//			}
//			System.out.println("dest:" + dest.bname + "[" + dest.bid + "]");
//			System.out.println(start.bname + " to " + dest.bname + ":" + call.success);
//
//			System.out.println("-------------kosu test-----------");
//			start = Network.bldgList[0];
//			dest = Network.bldgList[0];
//			System.out.println("start:" + start.bname + "[" + start.bid + "]");
//			for (int i = 0; i < 102;i++) {
//				Building d = Network.bldgList[i];
//				double b = start.kosuFinder(0, d);
//				System.out.println(start.bname + " -> " + d.bname + ":" + b);
//			}
//			System.out.println("-------------kosuTaken test-----------");
//			start = Network.bldgList[102];
//			for (int i = 0; i < 101;i++) {
//				Building d = Network.bldgList[i];
//				double b = start.kosuTakenFinder(23, d);
//				System.out.println("Taken" + " -> " + d.bname + ":" + b);
//			}
//			System.out.println("-------------start = dest test-----------");
//			dest = start;
//			call = new Call(start, dest, 23);
//			for (int i = 0; i < 100; i++) {
//				call = new Call(start, dest, 23);
//			}
//			System.out.println("start:" + start.bname + "[" + start.bid + "]");
//			System.out.println("dest:" + dest.bname + "[" + dest.bid + "]");
//			System.out.println(start.bname + " to " + dest.bname + ":" + call.success);
//			System.out.println("study has ended successfully");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
