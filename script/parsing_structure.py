import os
import javalang

def extract_java_files(directory):
    java_files = []
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(".java"):
                java_files.append(os.path.join(root, file))
    return java_files

def parse_java_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        content = file.read()
    return javalang.parse.parse(content)

def extract_class_info(tree):
    class_info = []
    package_name = None
    for path, node in tree:
        if isinstance(node, javalang.tree.PackageDeclaration):
            package_name = node.name
        if isinstance(node, javalang.tree.ClassDeclaration):
            class_name = node.name
            member_variables = []
            methods = []
            for field in node.fields:
                for declarator in field.declarators:
                    member_variables.append({
                        'type': field.type.name,
                        'name': declarator.name
                    })
            for method in node.methods:
                methods.append({
                    'access_modifier': method.modifiers,
                    'name': method.name,
                    'parameters': [(param.type.name, param.name) for param in method.parameters]
                })
            class_info.append({
                'package_name': package_name,
                'class_name': class_name,
                'member_variables': member_variables,
                'methods': methods
            })
    return class_info

def write_output(class_info, output_file):
    with open(output_file, 'w', encoding='utf-8') as file:
        for info in class_info:
            file.write(f"Package: {info['package_name']}\n")
            file.write(f"Class: {info['class_name']}\n")
            file.write("  Member Variables:\n")
            for var in info['member_variables']:
                file.write(f"    {var['type']} {var['name']}\n")
            file.write("  Methods:\n")
            for method in info['methods']:
                params = ', '.join([f"{param[0]} {param[1]}" for param in method['parameters']])
                file.write(f"    {' '.join(method['access_modifier'])} {method['name']}({params})\n")
            file.write("\n")

def main():
    directory = 'src/main/java/org/example/demoboard'
    output_file = 'script/out/java_signitures.txt'

    java_files = extract_java_files(directory)
    class_info = []

    for java_file in java_files:
        tree = parse_java_file(java_file)
        class_info.extend(extract_class_info(tree))

    write_output(class_info, output_file)

if __name__ == "__main__":
    main()