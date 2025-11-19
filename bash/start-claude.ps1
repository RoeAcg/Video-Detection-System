
# start-claude.ps1
# 清理旧的登录 Key
Remove-Item Env:\ANTHROPIC_AUTH_TOKEN -ErrorAction SilentlyContinue
Remove-Item Env:\ANTHROPIC_API_KEY -ErrorAction SilentlyContinue

# 设置 MiniMax-M2 模型环境变量
$env:ANTHROPIC_BASE_URL = "https://api.minimaxi.com/anthropic"
$env:ANTHROPIC_AUTH_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJHcm91cE5hbWUiOiLlvKDkuIDor7oiLCJVc2VyTmFtZSI6IuW8oOS4gOivuiIsIkFjY291bnQiOiIiLCJTdWJqZWN0SUQiOiIxOTg4OTU5MTMyNDc1OTkwOTk5IiwiUGhvbmUiOiIxNTExMTkwMTYwMiIsIkdyb3VwSUQiOiIxOTg4OTU5MTMyNDY3NjAyMzkxIiwiUGFnZU5hbWUiOiIiLCJNYWlsIjoiIiwiQ3JlYXRlVGltZSI6IjIwMjUtMTEtMTUgMTc6MTA6MjgiLCJUb2tlblR5cGUiOjQsImlzcyI6Im1pbmltYXgifQ.j-Dh7rep5Rb8y7CwbybwKfoh434uT8XzT22S2B2EPPD1-1CzYf5w2QiyzbjwFBCfhiNk-9GqohNicgShY7bvVFw-IVQCEZHFDXSY-yBmpoAFZxbeNpj2qwJR-TWyMWjPelrEDNxfAMD0IMeIsPiqkrSXDFxqkzx8A85rv8Hp3zcgd4VHzK8oFagB8FnSk6sozQhXZOuLxmsyn3oSkFDm8qIQ1IMbu_-3arP3fJ8cOBvJBjNQWuO_NoNSwtzeuWvDoHjGN60-VS5ktxn5NvhmYe-rNB3oxo9v4ZG8MTGjAmx6C1FBV3JJD_OFJRj7M-RffWhOCJvzCf72w6uy8zTuUg"
$env:ANTHROPIC_SMALL_FAST_MODEL = "MiniMax-M2"
$env:ANTHROPIC_DEFAULT_SONNET_MODEL = "MiniMax-M2"
$env:ANTHROPIC_DEFAULT_OPUS_MODEL = "MiniMax-M2"
$env:ANTHROPIC_DEFAULT_HAIKU_MODEL = "MiniMax-M2"

# 设置全局代理
$env:ALL_PROXY = "socks5://127.0.0.1:10808"

# 启动 Claude Code
claude

