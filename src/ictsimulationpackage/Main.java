package ictsimulationpackage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class Main {
	public static void main(String argv[]) {
		//�v�Z���Ԃ̎Z�o
		double calcTime ;
		calcTime = System.nanoTime();
		//24����
		int hour = 5;
		//�ꎞ��=60��
		int timelength = hour * 60;
		// �o�̓t�@�C��
		int filenum = 0;
		// �o�̓t�H���_
		String folder = "/Users/jaga/Documents/domain_project/test_output/test";

		//hakai�Ƃ́H
		for (int hakai = 0; hakai < 1; hakai++) {
			//bai�͔j�󂷂郊���N�̐��H
			for (int bai = 1; bai < 2; bai++) {
				// for(int b = 40; b < 41; b++){
				
				//�r���f�B���O���X�g�I�u�W�F�N�g�̍쐬
				BuildingList bldgs = new BuildingList(102);
				
				//�S�Ẵ����N���̎擾
				ArrayList<Link> allLinks = bldgs.allLinkList;
				
				double m = bai * 5; // ���v�����{��
				
				// CallExist[t] = ����t�ɑ��݂���Ă̐�
				int CallExist[] = new int[timelength];
				
				// CallOccur[t] = ����t�ɐ��N�����Ă̐�
				int CallOccur[] = new int[timelength];
				
				// CallLoss[t] = ����t�ɑ��������Ă̐�
				int CallLoss[] = new int[timelength];
				
				// CallLossRate[t] = ����t�̌đ���
				double CallLossRate[] = new double[timelength];
				
				//1�����N�݌v�����
				int kaisensu = (int) (221486 * 0.5); //���
				int outKaisensu = 78276; //��O
				
				//��������N�̉�����ݒ�
				for(Link ln :  bldgs.linkList){
					ln.iniCap(timelength, kaisensu );
				}
				//������p�����N�̉�����ݒ�
				for(Link ln : bldgs.exLinkList){
					ln.iniCap(timelength, kaisensu );
				}
				//��O���p�����N�̉�����ݒ�
				bldgs.outLink.iniCap(timelength, outKaisensu);

				// �j�󃊃��N�̐ݒ�
				int broken[] = { 69, 70, 76, 86 };

//				// �j�󃊃��N�̃L���p�V�e�B�ݒ� amount�[���j��(�Г�50%?)
				double ammount = 0.5 * hakai; //=0 �j��̓��[�v�ŉ�邪0�������Ȃ��B�Ȃ񂾂���H
//				for (int i = 0; i < broken.length; i++) {
//					//��ꂽ�����N��t=0�ɂ���������
//					capacity[0][broken[i]] = (int) (capacity[0][broken[i]] * ammount);//=0
//					//��ꂽ�����N�̐݌v�����in
//					kaisen[broken[i]] = (int) (kaisen[broken[i]] * ammount); //=0
//				}

				// listofcalllist.get(t) = ����t�ɂ����Đ��N�����Ẵ��X�g
				ArrayList<ArrayList<Call>> listofcalllist = new ArrayList<ArrayList<Call>>();
				//max.get(t) = ����t�ɂ����Ĕ��������Ă̒��ōł��I�������̒x�����̂̒l
				ArrayList<Integer> max = new ArrayList<Integer>();

				// callETlist[t] = ����t�ɏI������Ẵ��X�g
				@SuppressWarnings("unchecked")
				ArrayList<Call> callETlist[] = new ArrayList[timelength + 100];//�Ȃ��+100?
				for (int i = 0; i < callETlist.length; i++) {
					//������
					callETlist[i] = new ArrayList<Call>();
				}

				// ���ԃ��[�v�̊J�n
				for (int t = 0; t < timelength; t++) {
					System.out.println("���� : " + (t + 1));
					// capacity�ƌ����Ă͕ۑ������
					if (t > 0) {
						for(Link ln : allLinks){
							ln.capUpdate(t);
						}
						CallExist[t] = CallExist[t - 1];
					}

					// ���̎��Ԃɂ�����Ẵ��X�g
					ArrayList<Call> calllist = new ArrayList<Call>();
					// �Ă̐���.
					 System.out.println("�Ă̐����J�n");
					int M = t;//���x���������Ă̏I�������̍Œx�l
					int num = 0; //�V���ɔ��������Ă̐��H
					// �S�Ẵr���̑g�ݍ��킹��ʂ��čl���Ă���B�[��index
					for (int i = 0; i < 104 * 104; i++) {
						Kosu kosu = new Kosu(i, t);
						 System.out.println( (t+1)+ "���F" + i + "�Z�b�g�ڂ̌Đ�get");
						// �Đ����������A�Đ������m����p���ċ��߂�
						int occur = kosu.Occurrence(m);
//						 System.out.println("�|�A�\���ߒ�����");
						// ���肵���Đ��������ɏ]���ČĂ𐶋N����
						for (int j = 0; j < occur; j++) {
							Call call = new Call(i, t, broken, ammount);// 0����ȊO����n�߂�ꍇ�A+60*(�J�n����-1)
							calllist.add(call);
							num++;
							if (call.EndTime > M) {
								M = call.EndTime;
							}
							call = null;
//							 System.out.println(i + "�Z�b�g�ڂ�" + j + "�ڂ̌Đ���");
//							 System.out.println(t + "���ʍ��v" + num);
						}
					}
					max.add(M);
					listofcalllist.add(calllist);
					// calllist.clear();
					CallOccur[t] = listofcalllist.get(t).size();
					 System.out.println("�Ă̐�������");

					// ����t�ɐ��N�������ꂼ��̌Ăɂ��ĒʐM�̐���or���s��]��
					 System.out.println("�ʐM�̐��ە]���J�n");
					Collections.shuffle(listofcalllist.get(t));//LIST�̒��g���V���b�t��
					for (int i = 0; i < listofcalllist.get(t).size(); i++) {
						if (listofcalllist.get(t).get(i).LinkList[0] == -1) {
							//�g�p���郊���N�����݂��Ȃ��ꍇ
							CallLoss[t] += 1;
						} else {
							boolean check = true;
							//�����Nj�ɑ΂��ĉ���]�T�����݂��邩�ǂ���
							for (int j = 0; j < 103; j++) {
								if (listofcalllist.get(t).get(i).LinkList[j] > 0) {
									check = capacity[t][j] >= listofcalllist.get(t).get(i).LinkList[j];
									if (check == false) {
										break;
									}
								}
							}
							if (check) {
								// EndTime�ɏI���Ă�callList[EndTime]�ɉ�����
								callETlist[listofcalllist.get(t).get(i).EndTime].add(listofcalllist.get(t).get(i));
								// 0����ȊO����n�߂�ꍇ�AEndTime-60*(�J�n����-1)
								// �e�����N�̋󂫗e�ʂ��X�V
								for (int k = 0; k < 103; k++) {
									capacity[t][k] -= listofcalllist.get(t).get(i).LinkList[k];
								}
								CallExist[t] += 1;
							}else{
								// ������
								CallLoss[t] += 1;
							}
						}
					}
					// System.out.println("�ʐM�̐��ە]������");
					listofcalllist.get(t).clear();

					System.out.println("�����F" + (t + 1));// 0����ȊO����n�߂�ꍇ�A+60*(�J�n����-1)
					for (int i = 0; i < 103; i++) {
						System.out.print(capacity[t][i] + ",");
					}
					System.out.println();

					// ����t�ɏI������Ă�����
					 System.out.println("�Ă̏����J�n");
					for (int i = 0; i < callETlist[t].size(); i++) {
						for (int j = 0; j < 103; j++) {
							capacity[t][j] += callETlist[t].get(i).LinkList[j];
						}
						CallExist[t] -= 1;
					}
					callETlist[t].clear();
					 System.out.println("�Ă̏�������");
					 
					//�L�鎞�ԑ�i�ɂ����鐶�N�Ă̍Œ��I�����Ԃ��K�ꂽ�炻�̃��X�g��j�󂷂�
					for (int i = 0; i < max.size(); i++) {
						// System.out.println(max.get(i));
						if (max.get(i) == t) {// 0����ȊO����n�߂�ꍇ�A+60*(�J�n����-1)
							listofcalllist.get(i).clear();
						}
					}

					// �đ����̎Z�o
					CallLossRate[t] = ((double) CallLoss[t] * 100) / (double) CallOccur[t];

					System.out.println("���N�F" + CallOccur[t] + ", �����F" + CallLoss[t]);
					System.out.println("�đ����F" + CallLossRate[t]);
					System.out.println("���݁F" + CallExist[t]);

				}

				// �t�@�C���o��
				Calendar c = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MMdd_HHmmss");
				// System.out.println(sdf.format(c.getTime()));
				// String format = sdf.format(c.getTime());
				String format = String.valueOf(filenum);
				File datedir = new File(folder + format);
				datedir.mkdir();
				// summary
				try {
					File file = new File(folder + format + "_summary.txt");
					FileWriter filewriter = new FileWriter(file);
					filewriter.write("���v" + m + "�{");
					filewriter.write("  ");
					filewriter.write("�j�󃊃��N�F" + broken);
					filewriter.write("  ");
					filewriter.write("�e��" + ammount + "�{");
					filewriter.close();
				} catch (IOException e) {
					System.out.println(e);
				}

				// ���N
				File Occurrencedir = new File(folder + format + "_�Ă̐��N");
				Occurrencedir.mkdir();
				try {
					// �o�͐���쐬����
					FileWriter fw = new FileWriter(folder + format + "_�Ă̐��N_all.csv", true); // ���P
					PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
					for (int i = 0; i < CallOccur.length; i++) {
						// ���e���w�肷��
						pw.println(CallOccur[i]);
					}
					// �t�@�C���ɏ����o��
					pw.close();
					System.out.println("�Ă̐��N�̏o�͂��������܂����B");
				} catch (IOException ex) {
					// ��O������
					ex.printStackTrace();
				}

				// ����
				File Lossdir = new File(folder + format + "_������");
				Lossdir.mkdir();
				try {
					// �o�͐���쐬����
					FileWriter fw = new FileWriter(folder + format + "_������_all.csv", true); // ���P
					PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
					for (int i = 0; i < CallLoss.length; i++) {
						// ���e���w�肷��
						pw.println(CallLoss[i]);
					}
					// �t�@�C���ɏ����o��
					pw.close();
					System.out.println("�����Ă̏o�͂��������܂����B");
				} catch (IOException ex) {
					// ��O������
					ex.printStackTrace();
				}

				// �đ���
				File CallLossRatedir = new File(folder + format + "_�đ���");
				CallLossRatedir.mkdir();
				try {
					// �o�͐���쐬����
					FileWriter fw = new FileWriter(folder + format + "_�đ���_all.csv", true); // ���P
					PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
					for (int i = 0; i < CallLossRate.length; i++) {
						// ���e���w�肷��
						pw.println(CallLossRate[i]);
					}
					// �t�@�C���ɏ����o��
					pw.close();
					System.out.println("�đ����̏o�͂��������܂����B");
				} catch (IOException ex) {
					// ��O������
					ex.printStackTrace();
				}

				// ����
				File Existdir = new File(folder + format + "_���݌Đ�");
				Existdir.mkdir();
				try {
					// �o�͐���쐬����
					FileWriter fw = new FileWriter(folder + format + "_���݌Đ�_all.csv", true); // ���P
					PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
					for (int i = 0; i < CallExist.length; i++) {
						// ���e���w�肷��
						pw.println(CallExist[i]);
					}
					// �t�@�C���ɏ����o��
					pw.close();

					System.out.println("���݌Đ��̏o�͂��������܂����B");
				} catch (IOException ex) {
					// ��O������
					ex.printStackTrace();
				}
				filenum += 1;

				// }
			}
		}
		calcTime = System.nanoTime() - calcTime;
		System.out.println("�v�Z���ԁF" + (calcTime*(Math.pow(10,-9))) + "s");
		System.out.println("�v�Z���ԁF" + calcTime + "ns");
	}
}
