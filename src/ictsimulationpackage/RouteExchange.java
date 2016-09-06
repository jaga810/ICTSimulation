package ictsimulationpackage;

import java.io.FileInputStream;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class RouteExchange {
	static String[] BldgName = Settings.BldgName();
	static int[] exchangeID = Settings.exchangeID();

	//�����N��ʂ������̐��B���p����g�p�@�ǂ��̃r��������ʂ�������return
	static int[] route(String hatsu, String chaku, int[] broken, double ammount) {
		//hatsu:���M�r�� chaku:���M�r�� broken:��ꂽ�����N ammount:�j�󃊃��N�̃L���p�V�e�B

		//�ϐ��̏�����
		int[] links = new int[103]; //���������N�̎g�p��
		int[] hatsu_HatsuEx = new int[103];//Ex�ł��Ă����������p�r���܂ł̃��[�g
		int[] HatsuEx_ChakuEx = new int[103];//102�͗��n����@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@�@��O���p�m�[�h
		int[] ChakuEx_chaku = new int[103];

		//������ɋ�O���܂܂�Ă����ꍇ
		if (hatsu.equals("�����n��") || hatsu.equals("����")) {
			hatsu = "���n";
			links[102] += 1;
		}
		if (chaku.equals("�����n��") || chaku.equals("����")) {
			chaku = "���n";
			links[102] += 1;
		}
		//�r���f�B���O�̖��O�̕ۑ�
		String HatsuBldgName = BldgName[0];
		String ChakuBldgName = BldgName[0];
		String HatsuExchange = BldgName[0];
		String ChakuExchange = BldgName[0];

		int i = 0;
		while (HatsuBldgName.equals(hatsu) == false) {
			HatsuBldgName = BldgName[i];
			HatsuExchange = BldgName[exchangeID[i]];
			i++;//index�Ǘ������炱��Ȗʓ|�Ȃ��Ƃ�...
		}
		i = 0;
		while (ChakuBldgName.equals(chaku) == false) {
			ChakuBldgName = BldgName[i];
			ChakuExchange = BldgName[exchangeID[i]];
			i++;
		}
		//�j�󂳂�Ă��邩�ǂ������H
		boolean check = false;
		
		if (!hatsu.equals(chaku)){
			//�o���_���������p�r���܂�
			hatsu_HatsuEx = RouteStraight.route(hatsu, HatsuExchange, broken, ammount);
			if (hatsu_HatsuEx[0] == -1) {//RouteStraight.route��-1�Ƃ�Ȃ��񂾂�...
				check = true;
			}
			//�o���_��������p�r�����瓞���_��������p�r��
			HatsuEx_ChakuEx = RouteStraight.route(HatsuExchange, ChakuExchange, broken, ammount);
			if (HatsuEx_ChakuEx[0] == -1) {
				check = true;
			}
			//������p�r�����瓞���r���܂�
			ChakuEx_chaku = RouteStraight.route(ChakuExchange, chaku, broken, ammount);
			if (ChakuEx_chaku[0] == -1) {
				check = true;
			}
		}
		
		if (check) {
			for (int j = 0; j < 103; j++) {
				links[j] = -1;
			}

		} else {
			for (int j = 0; j < 103; j++) {
				links[j] = links[j] + hatsu_HatsuEx[j] + HatsuEx_ChakuEx[j] + ChakuEx_chaku[j];
			}
		}
		return links;
	}

}
