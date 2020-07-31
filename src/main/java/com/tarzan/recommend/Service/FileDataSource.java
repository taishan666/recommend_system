package com.tarzan.recommend.Service;

import com.tarzan.recommend.dto.ItemDTO;
import com.tarzan.recommend.dto.RelateDTO;
import com.tarzan.recommend.dto.UserDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Data
@Slf4j
public class FileDataSource {


      public final static String folderPath="F:\\ml-100k";


    /**
     * 方法描述: 读取基础数据
     *
     * @param
     * @Return {@link List< RelateDTO>}
     * @throws
     * @author tarzan
     * @date 2020年07月31日 16:53:40
     */
    public static List<RelateDTO> getData() {
        List<RelateDTO> relateList = Lists.newArrayList();
        try {
            FileInputStream out = new FileInputStream(folderPath+"\\u.data");
            InputStreamReader reader = new InputStreamReader(out, StandardCharsets.UTF_8);
            BufferedReader in = new BufferedReader(reader);
            String line = null;
            while ((line = in.readLine()) != null) {
                String newline = line.replaceAll("[\t]", " ");
                String[] ht = newline.split(" ");
                Integer userId = Integer.parseInt(ht[0]);
                Integer movieId = Integer.parseInt(ht[1]);
                Integer rating = Integer.parseInt(ht[2]);
                RelateDTO dto = new RelateDTO(userId, movieId, rating);
                relateList.add(dto);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return relateList;
    }

    /**
     * 方法描述: 读取用户数据
     *
     * @param
     * @Return {@link List< UserDTO>}
     * @throws
     * @author tarzan
     * @date 2020年07月31日 16:54:51
     */
    public static List<UserDTO> getUserData() {
        List<UserDTO> userList = Lists.newArrayList();
        try {
            FileInputStream out = new FileInputStream(folderPath+"\\u.user");
            InputStreamReader reader = new InputStreamReader(out, StandardCharsets.UTF_8);
            BufferedReader in = new BufferedReader(reader);
            String line = null;
            while ((line = in.readLine()) != null) {
                String newline = line.replaceAll("[\t]", " ");
                String[] ht = newline.split("\\|");
                Integer id = Integer.parseInt(ht[0]);
                Integer age = Integer.parseInt(ht[1]);
                String sex = ht[2];
                String profession = ht[3];
                String postcode = ht[4];
                UserDTO dto = new UserDTO(id, age, sex, profession, postcode);
                userList.add(dto);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return userList;
    }


    /**
     * 方法描述: 读取电影数据
     *
     * @param
     * @Return {@link List< ItemDTO>}
     * @throws
     * @author tarzan
     * @date 2020年07月31日 16:54:22
     */
    public static List<ItemDTO> getItemData() {
        List<ItemDTO> itemList = Lists.newArrayList();
        try {
            FileInputStream out = new FileInputStream(folderPath+"\\u.item");
            InputStreamReader reader = new InputStreamReader(out, StandardCharsets.UTF_8);
            BufferedReader in = new BufferedReader(reader);
            String line = null;
            while ((line = in.readLine()) != null) {
                String newline = line.replaceAll("[\t]", " ");
                String[] ht = newline.split("\\|");
                Integer id = Integer.parseInt(ht[0]);
                String name = ht[1];
                String date = ht[2];
                String link = ht[3];
                ItemDTO dto = new ItemDTO(id, name, date, link);
                itemList.add(dto);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return itemList;
    }


}

