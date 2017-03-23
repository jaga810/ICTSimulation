package ictsimulationpackage;

/**
 * Created by jaga on 2/24/17.
 */
public enum Setting {
    BUILDING_NUM(102),      //中継ビルの数（仮想的な県外ビルは除く）
    LOCAL_RING_NUM(3),      //ローカルリングの数
    SIMULATION_HOUR(24),  //シミュレーションする時間(h)。一回のシミュレーションは*60steps行われる

    LOCAL_LINK_CAPACITY(11200 * 2),
    KUNAI_LINK_CAPACITY(16300 * 2),
    KUGAI_LINK_CAPACITY(33200 * 2);

    private int num;

    Setting(int num) {
        this.num = num;
    }

    public int get() {
        return num;
    }
}
