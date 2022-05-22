out_file_path="$(pwd)/week_log.txt"

declare -A api_map
api_map["slipper-v3"]="智能卡口"
api_map["ship-face-api"]="船脸识别"
api_map["bianjian-ship-api"]="边检系统"

declare -A git_map
git_map["feat:"]="新增了:"
git_map["fix:"]="修复了:"
git_map["docs:"]="文档改动:"
git_map["style:"]="样式改动:"
git_map["refactor:"]="重构了:"
git_map["test:"]="测试了:"
git_map["chore:"]="构建或工具改动:"

workspace_paths=(
	"C:/Users/Administrator/IdeaProjects/slipper-v3"
	"C:/Users/Administrator/IdeaProjects/ship-face-api"
	"C:/Users/Administrator/IdeaProjects/bianjian-ship-api"
)

echo "---" >"$out_file_path"
echo "**($(date -d last-week '+%Y-%m-%d %H:%M:%S'), $(date '+%Y-%m-%d %H:%M:%S'))工作内容统计:**" >>"$out_file_path"

for workspace_path in "${workspace_paths[@]}"; do
	{
		cd "$workspace_path"
		git_log_out=$(git log --pretty=format:"	- \`%cd\` %cn%s" --since=1.weeks --date=format:"%a" --author="xing" --no-merges | grep -v "ignored")
		for git_map_key in "${!git_map[@]}"; do
			git_log_out=${git_log_out//$git_map_key/${git_map[$git_map_key]}}
		done
		[ -z "${git_log_out}" ] || {
			echo "- ${api_map[${workspace_path##*/}]}:" >>"$out_file_path"
			echo "${git_log_out}" >>"$out_file_path"
		}

		echo '' >>"$out_file_path"
	} || return
done
