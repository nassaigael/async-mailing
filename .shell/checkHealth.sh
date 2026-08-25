sudo apt-get install jq
export API_URL_SSM="`aws ssm get-parameter --name /demo-13e06d9b/$1/api/url`"
export API_URL=`echo $API_URL_SSM | jq -r '.Parameter.Value'`
curl --fail "$API_URL$2"