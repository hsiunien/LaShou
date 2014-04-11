package cn.duocool.lashou.net.client;

/**
 * ���ڼ������ػ��ߣ��ϴ��ĵĽ��
 * @author xwood
 *
 */
public interface NetTranProgressListener {
	public void onTransmitting(int requestCode,long nowProgress,long maxProgress);
}
