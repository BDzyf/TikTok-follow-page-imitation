import mysql.connector
import os
from PIL import Image, ImageDraw, ImageFont
import colorsys

# 数据库配置
config = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',
    'database': 'follow_db',
    'charset': 'utf8mb4'
}

# 头像文件夹：直接写死 Windows 绝对路径
AVATAR_DIR = r"C:\zyfykkk\yingyong\Android\AndroidStudioProjects\follow-backend\src\main\resources\static\avatars"
os.makedirs(AVATAR_DIR, exist_ok=True)

def generate_avatar(filename, index):
    """生成不同样式的占位头像（200x200）"""
    # 使用索引生成不同的颜色（HSL转RGB，保证颜色分布均匀）
    hue = (index * 137.5) % 360  # 黄金角间隔，避免相似色相邻
    rgb = colorsys.hls_to_rgb(hue / 360, 0.65, 0.7)  # 亮度0.65，饱和度0.7
    bg_color = tuple(int(c * 255) for c in rgb)
    
    # 创建图像
    img = Image.new('RGB', (200, 200), color=bg_color)
    draw = ImageDraw.Draw(img)
    
    # 添加装饰性几何图形
    # 根据索引决定图案类型，避免随机性以保证可重复生成
    if index % 3 == 0:
        # 画几个圆点
        for i in range(3):
            x = 40 + (i * 40)
            y = 40 + ((index + i) % 3 * 40)
            draw.ellipse([x, y, x+30, y+30], fill=(255, 255, 255, 180))
    elif index % 3 == 1:
        # 画方块
        for i in range(2):
            x = 50 + (i * 60)
            y = 50 + ((index + i) % 2 * 60)
            draw.rectangle([x, y, x+40, y+40], fill=(255, 255, 255, 180))
    else:
        # 画三角形
        for i in range(2):
            x = 60 + (i * 50)
            y = 70 + ((index + i) % 2 * 30)
            draw.polygon([(x, y), (x+20, y+40), (x-20, y+40)], fill=(255, 255, 255, 180))
    
    # 中心绘制大号数字
    try:
        # 尝试加载系统字体
        font = ImageFont.truetype("arial.ttf", 80)
    except:
        # 如果找不到字体文件，使用默认字体
        font = ImageFont.load_default()
    
    text = str(index)
    # 获取文字尺寸
    bbox = draw.textbbox((0, 0), text, font=font)
    text_width = bbox[2] - bbox[0]
    text_height = bbox[3] - bbox[1]
    
    # 计算居中位置
    x = (200 - text_width) / 2
    y = (200 - text_height) / 2
    
    # 绘制白色文字（带黑色描边增强可读性）
    draw.text((x-2, y), text, font=font, fill=(0, 0, 0))
    draw.text((x+2, y), text, font=font, fill=(0, 0, 0))
    draw.text((x, y-2), text, font=font, fill=(0, 0, 0))
    draw.text((x, y+2), text, font=font, fill=(0, 0, 0))
    draw.text((x, y), text, font=font, fill=(255, 255, 255))
    
    img.save(os.path.join(AVATAR_DIR, filename))

def generate_data():
    conn = mysql.connector.connect(**config)
    cursor = conn.cursor()
    
    # 清空数据
    cursor.execute("TRUNCATE TABLE follow")
    
    print("正在生成1000个头像...")
    for i in range(1, 1001):
        generate_avatar(f"{i}.jpg", i)
        if i % 100 == 0:
            print(f"已生成 {i}/1000 个头像")
    
    print("正在写入数据库...")
    sql = """
    INSERT INTO follow (douyin_id, nick, avatar, is_special, remark, follow_time, status)
    VALUES (%s, %s, %s, %s, %s, %s, %s)
    """
    
    batch_size = 100
    values = []
    
    for i in range(1, 1001):
        values.append((
            f"dy_{i}",
            f"用户{i}",
            f"/avatars/{i}.jpg",  # 相对路径
            1 if i % 20 == 0 else 0,  # 每20个设一个特别关注
            "",
            1764047780230 + i,
            1
        ))
        
        if len(values) >= batch_size:
            cursor.executemany(sql, values)
            conn.commit()
            values = []
            print(f"已插入 {i} 条数据")
    
    if values:
        cursor.executemany(sql, values)
        conn.commit()
    
    cursor.close()
    conn.close()
    print(f"✅ 完成！头像路径：{os.path.abspath(AVATAR_DIR)}")

if __name__ == '__main__':
    generate_data()