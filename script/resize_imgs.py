import os
from PIL import Image

def resize_image(image_path, output_folder, scale=0.1):
    with Image.open(image_path) as img:
        # 원본 크기 가져오기
        original_size = img.size
        # 크기 조정
        new_size = (int(original_size[0] * scale), int(original_size[1] * scale))
        resized_img = img.resize(new_size, Image.Resampling.LANCZOS)

        # 출력 경로 설정
        base_name = os.path.basename(image_path)
        output_path = os.path.join(output_folder, base_name)

        # 이미지 저장
        resized_img.save(output_path, optimize=True, quality=85)
        print(f"Resized {image_path} and saved to {output_path}")

def resize_images_in_folder(input_folder, output_folder, scale=0.1):
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    for root, dirs, files in os.walk(input_folder):
        for file in files:
            if file.lower().endswith(('.png', '.jpg', '.jpeg')):
                image_path = os.path.join(root, file)
                resize_image(image_path, output_folder, scale)

if __name__ == "__main__":
    input_folder = r"D:\developFuture\spring\demoboard\images\dall-e-imgs"
    output_folder = r"D:\developFuture\spring\demoboard\images\compressed"
    resize_images_in_folder(input_folder, output_folder, scale=0.1)