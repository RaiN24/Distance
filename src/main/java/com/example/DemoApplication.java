package com.example;

import com.example.domain.Result;
import com.example.service.DistanceService;
import com.google.gson.JsonObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RestController
@SpringBootApplication
public class DemoApplication  extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		// TODO Auto-generated method stub
		return builder.sources(DemoApplication.class);
	}

	@RequestMapping("/")
	public ModelAndView index(){
		ModelAndView mv=new ModelAndView("index");
		return mv;
	}
	@RequestMapping("/test1")
	public String test1(){
		try {
			File excelFile = new File("C:\\Users\\rain\\Desktop\\distance.xlsx");
			Workbook wb=new XSSFWorkbook(new FileInputStream(excelFile));
			Sheet sheet=wb.getSheetAt(0);
			File us_file = new File("C:\\Users\\rain\\Desktop\\US.xlsx"); //创建文件对象
			Workbook us_wb=new XSSFWorkbook(new FileInputStream(us_file));
			Sheet us_sheet =us_wb.getSheetAt(0);
//			Workbook wb=new XSSFWorkbook();
//			Sheet sheet=wb.createSheet();
			Row row=null;
			Cell cell=null;
			//写一行逻辑
//			Executor executor = Executors.newScheduledThreadPool(100);
			for(int i=3;i<=us_sheet.getLastRowNum();i++){
				row=us_sheet.getRow(i);
				double cell0=row.getCell(1).getNumericCellValue();
				String origins=row.getCell(2).getStringCellValue();
				row=sheet.createRow(i-1);
				cell=row.createCell(0);
				cell.setCellValue(cell0);
				for (int j = i+1; j <= us_sheet.getLastRowNum(); j++) {
					cell=row.createCell(j-1);
					if(i==j){
						cell.setCellValue(0);
					}else{
						String destinations=us_sheet.getRow(j).getCell(2).getStringCellValue();
						//多线程调用版本
//						DistanceService service=new DistanceService();
//						service.setOrigins(origins);
//						service.setDestinations(destinations);
//						executor.execute(service);
						//单线程调用版本
						String distance =distance(origins.substring(0,origins.length()-10),destinations.substring(0,destinations.length()-10));
						cell.setCellValue(distance);
					}
				}
			}
			OutputStream os = new FileOutputStream(new File("C:\\Users\\rain\\Desktop\\distance.xlsx"));
			wb.write(os);
			os.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "test1";
	}

	@RequestMapping("/test2")
	public String test2(){
		Sheet sheet;
		try {
			File excelFile = new File("C:\\Users\\rain\\Desktop\\US.xlsx"); //创建文件对象
			Workbook wb=new XSSFWorkbook(new FileInputStream(excelFile));
			sheet =wb.getSheetAt(0);
			for(Row row:sheet){
				Cell cell=row.getCell(0);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "test2";
	}

	@RequestMapping(value="/distance",method= RequestMethod.GET)
	public String distance(@RequestParam("origins") String origins, @RequestParam("destinations") String destinations) throws Exception{
		String urlString = "https://maps.googleapis.com/maps/api/distancematrix/json?"
				+ "units=imperial&origins="+origins.replace(' ',
				',')+"&destinations=" +
				destinations.replace(' ',
						',') +"&key=AIzaSyDHIdh10mg1corNw5R336jAkScXt8ozGLQ";
		String res = "";
		try {
			URL url = new URL(urlString);
			java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			java.io.BufferedReader in = new java.io.BufferedReader(
					new java.io.InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				res += line + "\n";
			}
			in.close();
		} catch (Exception e) {
			System.out.println("error in return ,and e is " + e.getMessage());
		}
		com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
		String result="";
		try {
			JsonObject json = (JsonObject) parser.parse(res);
			String status = json.get("status").getAsString();
			if(!status.equals("OK")){
				return "error";
			}
			result=json.get("rows").getAsJsonArray().get(0).getAsJsonObject().get("elements")
					.getAsJsonArray().get(0).getAsJsonObject().get("distance").getAsJsonObject()
					.get("text").getAsString();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
