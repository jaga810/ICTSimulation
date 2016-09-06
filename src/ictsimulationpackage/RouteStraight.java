package ictsimulationpackage;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class RouteStraight {
	static ArrayList<Integer> LinkListPlus = Settings.LinkListPlus();
	static ArrayList<Integer> LinkListMinus = Settings.LinkListMinus();
	static String[] BldgName = Settings.BldgName();
	static int[] exchangeID = Settings.exchangeID();
	
	//route[i]�F�ԍ�i�̃r���ɌĂ��ʂ����񐔁B���p����g�p���Ȃ��ꍇ
	static int[] route(String hatsu, String chaku, int[] broken, double ammount) {
		int hatsuID = 0;
		int chakuID = 0;
		// String hatsu = "���n";
		// String chaku = "���n";
		String HatsuBldgName = BldgName[0];
		String ChakuBldgName = BldgName[0];
		// System.out.println(HatsuBldgName);
		// System.out.println(ChakuBldgName);
		int id = 0;
		// ���M�r����ID�擾
		while (HatsuBldgName.toString().equals(hatsu) == false) {
			HatsuBldgName = BldgName[id];
			hatsuID = id;
			id++;
		}
		id = 0;
		// �����r����ID�擾
		while (ChakuBldgName.toString().equals(chaku) == false) {
			ChakuBldgName = BldgName[id];
			chakuID = id;
			id++;
		}

		// System.out.println(hatsuID + "," + chakuID);

		// long stop = System.currentTimeMillis();

		// �g�p���������N�̃��X�g�H
		ArrayList<Integer> OccupiedLinks = new ArrayList<Integer>();

		// i�͔��M�r���̎��̃r���B�ŏI�r�������G�X�P�[�v
		int i;
		if (hatsuID == 101) {
			i = 0;
		} else {
			i = hatsuID + 1;
		}
		if (chakuID == 101) {
			// ���n�_��ID�̍ŏI���̂Ƃ�
			while (LinkListPlus.get(i) != 0) {
				// 0��101�ɂȂ�܂Ł{�����Ƀ����N�����ǂ�
				OccupiedLinks.add(LinkListPlus.get(i));
				i++;
			}
		} else {
			// ���n�_���r���ɂ��鎞
			while (LinkListPlus.get(i) != chakuID + 1) {
				// ���n�_�ɍs���܂Ń��[�v�𑱂���B���n�_��links�̍Ō�̗v�f�ɂȂ�
				OccupiedLinks.add(LinkListPlus.get(i));
				i++;
			}
		}

		// �j�󃊃��N���o�H��ɂ��邩�ǂ����̃`�F�b�N
		int checkID[] = new int[broken.length];
		boolean check = false;
		for (int j = 0; j < broken.length; j++) {
			// ��ꂽ�[���o�H�Ɋ܂܂�邩
			checkID[j] = OccupiedLinks.indexOf(broken[j]);
			if (checkID[j] > -1) {
				check = true;
			}
		}

		// �j�󃊃��N������ꍇ
		if (check) {
			Random rnd = new Random();
			int rand = rnd.nextInt(10); // 1~10�̊Ԃ̈�l�����𔭐�
			// amount�Ƃ́H�H�H�������A���̌o�H���j�󂳂��m����\���Ă�݂����H
			if (rand >= ammount * 10) {
				OccupiedLinks.clear();
				i = 101 - hatsuID;
				while (LinkListMinus.get(i) != chakuID) {
					//�t�o�H�̒T��
					OccupiedLinks.add(LinkListMinus.get(i));
					i++;
				}
			}
		}
		// }

		int links[] = new int[103];
		/*
		 * check = false; for(int j = 0; j < broken.length; j++){ checkID[j] =
		 * OccupiedLinks.indexOf(broken[j]); if(checkID[j] > -1){ check = true;
		 * } } if(check){ for(int j = 0; j < 103; j++){ links[j] = -1; } }
		 */
		// else{
		for (int j = 0; j < OccupiedLinks.size(); j++) {
			links[OccupiedLinks.get(j)] += 1;
		}
		// }
		OccupiedLinks.clear();
		// System.out.print(OccupiedLinks);
		// System.out.println();
		// System.out.println(stop-start);
		
		return (links);
	}
}
