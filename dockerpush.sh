#!/bin/sh
aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin public.ecr.aws/w9r5i4l2
./mvnw  package
docker ps --filter "ancestor=public.ecr.aws/w9r5i4l2/tessell/tessell-lcm-mcp:latest " -q | xargs -r docker stop
docker build -f src/main/docker/Dockerfile.legacy-jar -t tessell/tessell-lcm-mcp .
docker tag tessell/tessell-lcm-mcp:latest public.ecr.aws/w9r5i4l2/tessell/tessell-lcm-mcp:latest
docker push public.ecr.aws/w9r5i4l2/tessell/tessell-lcm-mcp:latest