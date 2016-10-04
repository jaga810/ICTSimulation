package ictsimulationpackage;

import java.util.ArrayList;

public class Call {
    // ����P�̌Ẵf�[�^
    static ArrayList<Call> limitList[];// �L�鎞�ԂɏI������Ẵ��X�g
    static int sumHoldTime[];
    //����Ŕ��������Ă̐������ƌđ����i�݌v�F���ԑS�̂Łj
    static long areaKosu[] = new long[102];
    static long exLossKosu[] = new long[102];
    //��O�Ŕ��������Ă̐������ƌđ����i�݌v�j
    static long exKosu[] = new long[102];
    static long areaLossKosu[] = new long[102];
    static long timeLength;

    Building start;
    Building dest;
    int EndTime;
    ArrayList<Link> LinkList;
    boolean success = false;

    static void reset(int length) {
        for (int i = 0; i < length; i++) {
            // ������
            limitList[i] = new ArrayList<Call>();
            sumHoldTime[i] = 0;
        }
        for (int i = 0; i < areaKosu.length; i++) {
            areaKosu[i] = 0;
            areaLossKosu[i] = 0;
            exKosu[i] = 0;
            exLossKosu[i] = 0;
        }
    }

    Call(int tLength) {
        sumHoldTime = new int[tLength];
        limitList = new ArrayList[tLength];
        for (int i = 0; i < tLength; i++) {
            // ������
            limitList[i] = new ArrayList<Call>();
        }
        timeLength = tLength;
        System.out.println("Call Class initialized with timelength:" + timeLength);
    }

    Call(Building start, Building dest, int time) {
        // time:���N�����Abroken[]:��ꂽ�����N
        this.start = start;
        this.dest = dest;
        // �I������
        int holdTime = HoldingTime.OneHoldingTime();
        this.EndTime = time + holdTime;
        sumHoldTime[time] += holdTime;
        // System.out.println("sum hold time" + sumHoldTime[time]);
        if (start == dest) {
            // �o���r���Ɠ����r���������ꍇ
            success = true;
        } else {
            // �g�p���郊���N
            this.LinkList = LargeRing.route(start, dest);
            if (this.LinkList != null) {
                // �ڑ��ɐ��������ꍇ
                for (Link ln : this.LinkList) {
                    ln.addCap();
                }
                success = true;
            }
        }

        //�Ă������ɐ��������ꍇ
        if (success && EndTime < timeLength) {
            // System.out.println("EndTime:" + EndTime);
            limitList[EndTime].add(this);
            // �Ă̔�����ʂ������N�ɑI�蕪����
            if (start.areaBldg != null && dest.areaBldg != null && start != dest &&
                    time < 14 * 60 && time > 12 * 60) {
                if ((start.areaBldg == dest.areaBldg)) {
                    for (Link ln : LinkList) {
                        if (ln.id < 102) {
                            areaKosu[ln.id]++;
                        }
                    }
                } else {
                    for (Link ln : LinkList) {
                        if (ln.id < 102) {
                            exKosu[ln.id]++;
                        }
                    }
                }
            }
        } else if (EndTime < timeLength) {
            //�Ă̐����Ɏ��s�����ꍇ
            // �Ă̔�����ʂ������N�ɑI�蕪����
//			if (start.areaBldg != null && dest.areaBldg != null && start != dest) {
//				if ((start.areaBldg == dest.areaBldg)) {
//					for (Link ln : LinkList) {
//						if (ln.id < 102) {
//							areaLossKosu[ln.id]++;
//						}
//					}
//				} else {
//					for (Link ln : LinkList) {
//						if (ln.id < 102) {
//							exLossKosu[ln.id]++;
//						}
//					}
//				}
//			}
        }
    }

    void delete() {
        if (LinkList == null) {
            return;
        }
        for (Link ln : LinkList) {
            if (!ln.subCap()) {
                System.out.println("capacity goes to 0");
                System.out.println(1 / 0);
            }
        }
    }
}
