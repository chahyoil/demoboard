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

def extract_class_base_path(node):
    for annotation in node.annotations:
        if annotation.name in ['RequestMapping', 'Controller']:
            if annotation.element:
                for element_pair in annotation.element:
                    element = element_pair[1]
                    if isinstance(element, javalang.tree.Literal):
                        return element.value.strip('"')
    return ""

def extract_method_path(annotation):
    if annotation.element:
        for element_pair in annotation.element:
            element = element_pair[1]
            if isinstance(element, javalang.tree.Literal):
                return element.value.strip('"')
    return ""

def extract_method_parameters(method_node):
    parameters = []
    for param in method_node.parameters:
        param_type = ''.join(param.type.name)
        param_name = param.name
        parameters.append(f"{param_type} {param_name}")
    return ", ".join(parameters)

def extract_endpoints(tree):
    endpoints = []

    for path, node in tree.filter(javalang.tree.ClassDeclaration):
        class_base_path = extract_class_base_path(node)

        for path, method_node in node.filter(javalang.tree.MethodDeclaration):
            annotations = {annotation.name: annotation for annotation in method_node.annotations}
            http_methods = [method for method in ['GetMapping', 'PostMapping', 'PutMapping', 'DeleteMapping'] if method in annotations]

            if http_methods:
                method_parameters = extract_method_parameters(method_node)
                for method in http_methods:
                    method_path = extract_method_path(annotations[method])
                    full_path = f"{class_base_path}{method_path}".replace('//', '/')
                    endpoint_info = {
                        'method': method,
                        'path': full_path,
                        'class_name': node.name,
                        'method_name': method_node.name,
                        'annotation_value': f'("{full_path}")',
                        'method_parameters': method_parameters
                    }
                    endpoints.append(endpoint_info)

    return endpoints

def write_output(endpoints, output_file):
    with open(output_file, 'w', encoding='utf-8') as file:
        for endpoint in endpoints:
            file.write(f"Class: {endpoint['class_name']}\n")
            file.write(f"  {endpoint['method']} / -> {endpoint['method_name']}({endpoint['method_parameters']}) {endpoint['annotation_value']}\n")
            file.write("\n")

def main():
    directory = 'src/main/java/org/example/demoboard/controller'
    output_file = 'script/out/endpoints_output.txt'

    if not os.path.exists(os.path.dirname(output_file)):
        os.makedirs(os.path.dirname(output_file))

    java_files = extract_java_files(directory)
    endpoints = []

    for java_file in java_files:
        tree = parse_java_file(java_file)
        endpoints.extend(extract_endpoints(tree))

    write_output(endpoints, output_file)

if __name__ == "__main__":
    main()