import shutil
from faker import Faker
fake = Faker('ko_KR')  # 한국어 이름 생성기
import time, uuid, random
import pandas as pd
import os, sys
import re
import subprocess
import json

def clean_data(value):
    try :
        # 화이트스페이스를 공백으로 치환
        value = ' '.join(value.split())
        # SQL 문법에서 문제가 될 수 있는 문자를 안전하게 치환
        value = value.replace("'", "_")
    except:
        return value
    return value

def copy_files(png_files, server_file_imgs, server_file_path):
    for f in png_files:
        f_name = f.split("\\")[-1]
        if f_name in server_file_imgs:
            new_file_name = server_file_imgs[f_name]
            new_file_path = os.path.join(server_file_path, new_file_name)
            shutil.copyfile(f, new_file_path)
            print(f"Copied {f} to {new_file_path}")

def clear_files(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            file_path = os.path.join(root, file)
            os.remove(file_path)

def find_png_files(directory):
    png_files = []
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(".png"):
                png_files.append(os.path.join(root, file))
    return png_files

def generate_insert_queries(df, category, allImgs, user_id_range=(1, 10), is_open=True):
    queries = []
    for index, row in df.iterrows():
        user_id = random.randint(*user_id_range)

        # 이미지 파일 필터링
        target_imgs = [f for f in allImgs if (category in f) and (str(index) in extract_numbers(f))]
        if target_imgs:
            orig_img_name = target_imgs[0]
            server_img_name = f"{int(time.time() * 1000)}-{uuid.uuid4()}{orig_img_name[orig_img_name.rfind('.'):]}"
            server_file_imgs[orig_img_name] = server_img_name
        else:
            orig_img_name = ""
            server_img_name = ""

        if category == "movie":
            title = clean_data(row['title'])
            content = (
                f"\t원제목 : {clean_data(row['original_title'])}\n"
                f"\t요약 : {clean_data(row['overview'])}\n"
                f"\t태그라인 : {clean_data(row['tagline'])}\n"
                f"\t주인공 : {clean_data(row['first_character'])}"
            )
            category_id = 2

        elif category == "book":
            title = clean_data(row['book_title'])
            content = (
                f"\t저자 : {clean_data(row['author'])}\n"
                f"\t요약 : {clean_data(row['book_details'])}\n"
                f"\t장르 : {clean_data(row['genres'])}"
            )
            category_id = 3

        elif category == "recipe":
            title = clean_data(row['name'])
            content = (
                f"\t설명 : {clean_data(row['description'])}\n"
                f"\t저자 : {clean_data(row['author'])}\n"
                f"\t재료 : {clean_data(row['ingredients'])}\n"
                f"\t조리법 : {clean_data(row['steps'])}"
            )
            category_id = 4

        elif category == "game":
            title = clean_data(row['Name'])
            content = (
                f"\t개발자 : {clean_data(row['Developer'])}\n"
                f"\t장르 : {clean_data(row['Genres'])}\n"
                f"\t설명 : {clean_data(row['Description'])}"
            )
            category_id = 5

        elif category == "music":
            title = clean_data(row['release_name'])
            content = (
                f"\t아티스트 : {clean_data(row['artist_name'])}\n"
                f"\t주 장르 : {clean_data(row['primary_genres'])}\n"
                f"\t부 장르 : {clean_data(row['secondary_genres'])}\n"
                f"\t설명 : {clean_data(row['descriptors'])}"
            )
            category_id = 6

        query = f"INSERT INTO post (user_id, title, content, is_open, category_id, original_filename, stored_filename) VALUES ({user_id}, '{title}', '{content}', {is_open}, {category_id}, '{orig_img_name}', '{server_img_name}');"
        queries.append(query)

    return queries

def generate_user_insert_query(num_users=10, default_role='ROLE_BRONZE'):
    user_queries = []
    role_queries = []
    for _ in range(num_users):
        username = fake.user_name()
        password = fake.password()
        email = fake.email()

        # Generate user insert query
        user_query = f"INSERT INTO users (username, password, email) VALUES ('{username}', '{password}', '{email}') RETURNING id;"
        user_queries.append(user_query)

        # Assume roles table already has the default_role and get its id
        role_query = f"""
        WITH inserted_user AS (
            {user_query}
        )
        INSERT INTO user_roles (user_id, role_id)
        SELECT inserted_user.id, role.id
        FROM inserted_user, role
        WHERE role.name = '{default_role}';
        """
        role_queries.append(role_query)

    return user_queries, role_queries

def extract_numbers(s):
    return re.findall(r'\d+', s)

dirList = ["music", "books", "games", "movies", "recipe"]
dataPath = "./images/dall-e-imgs/"

musicPath = os.path.join(dataPath, "music") + "/"
musicImgPath = os.path.join(musicPath, "imgout") + "/"

booksPath = os.path.join(dataPath, "books") + "/"
booksImgPath = os.path.join(booksPath, "imgout") + "/"

gamesPath = os.path.join(dataPath, "games") + "/"
gamesImgPath = os.path.join(gamesPath, "imgout") + "/"

moviesPath = os.path.join(dataPath, "movies") + "/"
moviesImgPath = os.path.join(moviesPath, "imgout") + "/"

recipePath = os.path.join(dataPath, "recipe") + "/"
recipeImgPath = os.path.join(recipePath, "imgout") + "/"

# 절대 경로를 만들어주는 함수
def get_image_paths(dir_name):
    base_path = os.path.join(dataPath, dir_name)
    img_path = os.path.join(base_path, "imgout")
    return [os.path.join(img_path, img) for img in os.listdir(img_path)]

print("read imgages start")

all_names = os.listdir(musicImgPath) + os.listdir(booksImgPath) + os.listdir(gamesImgPath) + \
    os.listdir(moviesImgPath) + os.listdir(recipeImgPath)

all_paths = []
for dir_name in dirList:
    all_paths.extend(get_image_paths(dir_name))

# print(all_paths)

query_str_path = "src/main/resources/3_posts.sql"

with open(query_str_path, 'rt', encoding = 'utf-8') as f :
    query_str = f.read()

# 정규 표현식 패턴 정의
pattern = re.compile(r"'([^']*)'")

def extract_filenames(query_str):
    all_results = []

    for ii, split0 in enumerate(query_str.split(';')) :
        print(ii)
        values_str = split0.split("VALUES")[-1]

        # 작은 따옴표 안의 내용 추출
        extracted_values = pattern.findall(values_str)

        if len(extracted_values) >= 2:
            original_filename = extracted_values[-2]
            stored_filename = extracted_values[-1]

            # 결과 딕셔너리 생성
            result_dict = {original_filename: stored_filename}
            all_results.append(result_dict)
        else:
            print(f"Skipping due to insufficient extracted values: {extracted_values}")

    return all_results

# 결과 추출
result = extract_filenames(query_str)

# 딕셔너리 형태로 변환
final_dict = {k: v for d in result for k, v in d.items()}
new_dict = {}

for k, v in final_dict.items():
    abs_paths = [abs_path for abs_path in all_paths if k in abs_path]
    if abs_paths:
        new_dict[abs_paths[0]] = v

# print(new_dict)

#### 원본 파일 절대경로와 서버 파일 경로 매핑
### docker volume inspect demoboard_spring_data 을 입력해서 해당 경로를 할당
# Docker 볼륨 이름
volume_name = "demoboard_spring_data"
container_name = "demoboard_spring"

# Docker 볼륨의 마운트 포인트 가져오기
def get_docker_volume_mountpoint(volume_name):
    try:
        result = subprocess.run(["docker", "volume", "inspect", volume_name], capture_output=True, text=True)
        volume_info = json.loads(result.stdout)

        return volume_info[0]["Mountpoint"]
    except Exception as e:
        print(f"Error retrieving volume mountpoint: {e}")
        return None

# 마운트 포인트 가져오기
dest_docker_path = get_docker_volume_mountpoint(volume_name)
if not dest_docker_path:
    print("Unable to retrieve volume mountpoint.")
    exit(1)

# dest_docker_path = "/var/lib/docker/volumes/demoboard_spring_data/_data"
# 파일 복사
for original, stored in new_dict.items():
    try:
        dest_path = f"/app/userData/{stored}"
        # Docker cp 명령어 실행
        subprocess.run(["docker", "cp", original, f"{container_name}:{dest_path}"], check=True)
        print(f"Copied {original} to {dest_path} in container {container_name}")
    except subprocess.CalledProcessError as e:
        print(f"Error copying {original} to {dest_path} in container {container_name}: {e}")

