import os
import glob

basedir = r"c:\Users\tomim\Desktop\blindaje_backend_barrio\src\main\java\com\blindaje"
files = glob.glob(f"{basedir}/**/*.java", recursive=True)

suffixes = ["Repository", "Service", "Publisher", "Status", "Controller", "Adapter", "Filter", "Config", "Provider", "Interceptor", "Listener", "Dispatcher", "Response", "Request", "Context", "Type", "Entry", "Details"]

names = set()
for f in files:
    basename = os.path.basename(f)
    name = basename.replace(".java", "")
    names.add(name)

for name in sorted(names):
    print(name)
