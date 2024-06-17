import os
current_directory = os.getcwd()

def print_directory_contents(directory, prefix=''):
    for root, dirs, files in os.walk(directory):
        level = root.replace(directory, '').count(os.sep)
        indent = '-' * 4 * level
        print(f'{prefix}{indent}{os.path.basename(root)}/')
        sub_indent = '-' * 4 * (level + 1)
        for file in files:
            if file.endswith('.java'):
                print(f'{prefix}{sub_indent}{file}')

directory_path = current_directory + '/src/main/java/org/example/demoboard'

print_directory_contents(directory_path)