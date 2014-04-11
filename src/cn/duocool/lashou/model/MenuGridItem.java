package cn.duocool.lashou.model;

public class MenuGridItem {
		private int imageId;
		private String title;
		int bgColor;
		public MenuGridItem(int imgId,String title) {
		 this.setImageId(imgId);
		 this.setTitle(title);
		}
		public int getImageId() {
			return imageId;
		}
		public void setImageId(int imageId) {
			this.imageId = imageId;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
}
