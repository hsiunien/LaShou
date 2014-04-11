package cn.duocool.lashou.model;

import java.util.ArrayList;

//���ڱ�����Ҫ���Ӧ�ó�����Ϣ
public class AppModel 
{
	
	
         String packageName;
         int islock;
       
      
         ArrayList<Integer> ignoreWeek=new ArrayList<Integer>();
         long limitTime;
         boolean haveVerify=false;//当使用时间超过规定时间时，是否验证了密码
         String password;
         String passwordtype;
         String lockName;
         public AppModel(String packageName, 
  				ArrayList<Integer> ignoreWeek,String limitTime,String password,String passwordtype,String lockName) {
 			super();
 			this.packageName = packageName;
 			this.password=password;
 			this.passwordtype=passwordtype;
 			
 			this.ignoreWeek = ignoreWeek;
 			this.lockName=lockName;
 			long limitT;
 			   if(limitTime.equals("0"))
 			   {
 				   limitT=0;
 			   }else
 			   {
 				limitT=   Integer.parseInt(limitTime.substring(0,limitTime.indexOf("小时")))*60*60*1000+Integer.parseInt(limitTime.substring(limitTime.indexOf("小时")+2,limitTime.indexOf("分钟")))*60*1000;
 				   
 			   }
 			this.limitTime=limitT;
 		}
         public String getLockName() {
			return lockName;
		}
		public void setLockName(String lockName) {
			this.lockName = lockName;
		}
		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getPasswordtype() {
			return passwordtype;
		}

		public void setPasswordtype(String passwordtype) {
			this.passwordtype = passwordtype;
		}

		public boolean isHaveVerify() {
			return haveVerify;
		}

		public void setHaveVerify(boolean haveVerify) {
			this.haveVerify = haveVerify;
		}

		public long getLimitTime() {
			return limitTime;
		}

		public void setLimitTime(long limitTime) {
			this.limitTime = limitTime;
		}

		public long getUsedTime() {
			return usedTime;
		}
         public void addUsedTime(int time)
         {
        	 usedTime+=time;
         }
		public void setUsedTime(long usedTime) {
			this.usedTime = usedTime;
		}

		long usedTime=0;
         public ArrayList<Integer> getIgnoreWeek() {
			return ignoreWeek;
		}

		public void setIgnoreWeek(ArrayList<Integer> ignoreWeek) {
			this.ignoreWeek = ignoreWeek;
		}
    
		

		
      

		
         public int getIslock() 
         {
			return islock;
		}

		public void setIslock(int islock) {
			this.islock = islock;
		}

	

		

	
	

		public AppModel(String packageName) {
		
			this.packageName = packageName;
		}

		public String getPackageName() {
			return packageName;
		}

		public void setPackageName(String packageName) 
		{
			this.packageName = packageName;
		}
         
}
