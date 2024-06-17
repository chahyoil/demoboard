import os
import re
import subprocess
import json
import shutil

# 각 디렉토리 경로 설정
dataPath = "./images/compressed/"
dirList = ["music", "books", "games", "movies", "recipe"]

def get_image_paths(dataPath):

    if os.path.exists(dataPath):
        return [os.path.join(dataPath, img) for img in os.listdir(dataPath)]
    else:
        print(f"Directory does not exist: {dataPath}")
        return []

# 모든 파일 경로를 가져옴
all_paths = os.listdir(dataPath)

print(all_paths)

## SQL 쿼리 파일 경로
query_str_path = "./src/main/resources/3_posts.sql"

# SQL 쿼리 문자열 읽기
with open(query_str_path, 'rt', encoding='utf-8') as f:
    query_str = f.read()

# # 정규 표현식 패턴 정의
pattern = re.compile(r"'([^']*)'")

def extract_filenames(query_str):
    all_results = []

    for ii, split0 in enumerate(query_str.split(';')):
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
#
# 결과 추출
result = extract_filenames(query_str)
#
# 딕셔너리 형태로 변환
final_dict = {k: v for d in result for k, v in d.items()}
new_dict = {}

for k, v in final_dict.items():
    abs_paths = [abs_path for abs_path in all_paths if k in abs_path]
    if abs_paths:
        new_dict[abs_paths[0]] = v

print(new_dict)
#
# # Docker 볼륨 이름
volume_name = "demoboard_spring_data"
container_name = "demoboard_spring"
#
# # Docker 볼륨의 마운트 포인트 가져오기
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
#
# # 파일 복사
for original, stored in new_dict.items():
    try:
        dest_path = os.path.join(dest_docker_path, stored)
        shutil.copy2(original, dest_path)
        print(f"Copied {original} to {dest_path}")
    except Exception as e:
        print(f"Error copying {original} to {dest_path}: {e}")